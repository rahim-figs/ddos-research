package simulation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;


public class Topology {

	private ArrayList<RouterSet> rings;
	private ArrayList<Host> hosts;
	private RouterSet mAllRouters;
	private ArrayList<Rule> rules;
	
	private Simulation Simulation;
	
	public Topology(Simulation simulation) {
		this.Simulation = simulation;
		
		mAllRouters = new RouterSet("All Router set");
	}
	
	public Simulation getSimulation() {
		return Simulation;
	}
	
	public void initHosts(int number, int capacity, int windowLength) {
		hosts = new ArrayList<Host>(number);
		rules = new ArrayList<Rule>(number);
		
		for (int i = 1; i <= number; i++) {
			Rule rule = new Rule(i);
			Host host = new Host(i, capacity, windowLength, this);
			host.setRule(rule);
			
			rules.add(rule);
			hosts.add(host);
		}
	}
	
	public void initRouters(int firstLayerCount, double fanOutFactor, int numberOfLayers, int windowLength) {
		rings = new ArrayList<RouterSet>(numberOfLayers);
		
		int currentLayerCount = firstLayerCount;
		fanOutFactor += 1;
		
		for (int i = 1; i <= numberOfLayers; i++) {
			RouterSet ring = new RouterSet("Ring " + i);
			rings.add(ring);
			
			for (int j = 1; j <= currentLayerCount; j++) {
				Router router = new Router(i, j, 5, this);
				ring.addRouter(router);
				mAllRouters.addRouter(router);
				
				// Connect to hosts if this is the first ring
				if (i == 1) {
					for (Host host : hosts) {
						router.addSuccessor(host);
					}
				} else {
					// We randomly connect to a router from the inner ring
					RouterSet innerRing = rings.get(i - 2);
					int innerRouterNumber = new Random().nextInt(innerRing.getRouterSet().size());
					Router innerRouter = innerRing.getRouterSet().get(innerRouterNumber);
					router.addSuccessor(innerRouter);
				}
			}
			System.out.println("Added " + currentLayerCount + " routers in ring " + i);
			currentLayerCount = Math.max((int) (currentLayerCount * fanOutFactor), currentLayerCount + 1);
		}
	}
	
	public void computeRoutes() {
		
		for (int i = 0; i < rings.size(); i++) {
			
			RouterSet ring = rings.get(i);
			ArrayList<Router> routers = ring.getRouterSet();
			
			for (Router router : routers) {
				
				if (router.getRing() == 1) {
					// Add one-hop route to all hosts
					for (int h = 0; h < hosts.size(); h++) {
						router.addRoute(rules.get(h), hosts.get(h), 1);
					}
				} else {
					// Outer rings
					HashMap<Rule, Hop> possibleRoutes = new HashMap<Rule, Hop>();
					ArrayList<Node> successors = router.getSuccessors();
					for (Node successor : successors) {
						if (successor.getNodeType() == Node.NodeType.HOST) {
							continue;
						}
						
						Router successorRouter = (Router) successor;
						
						HashMap<Rule, Hop> successorRoutes = successorRouter.shareRoutes();
						for (Rule rule : successorRoutes.keySet()) {
							Hop existingRoute = possibleRoutes.get(rule);
							if (existingRoute == null || existingRoute.time > successorRoutes.get(rule).time + 1) {
								possibleRoutes.put(rule, new Hop(successorRouter, successorRoutes.get(rule).time + 1));
							}
						}
					}
					
					for (Rule rule : possibleRoutes.keySet()) {
						router.addRoute(rule, possibleRoutes.get(rule).node, possibleRoutes.get(rule).time);
					}
				}
			}
		}
	}
	
	public Attack createAttack(int ringNumber, int numberRouters, Rule rule, int begin, int interval, int steps, int numberPackets) {
		RouterSet ring = rings.get(ringNumber - 1);
		ArrayList<Router> attackRouters = new ArrayList<Router>(ring.getRouterSet());
		while (attackRouters.size() > numberRouters) {
			attackRouters.remove((int) Math.random() * attackRouters.size());
		}
		
		return new Attack(attackRouters, rule, begin, interval, steps, numberPackets);
	}
	
	public void generateAttack(Attack attack) {
		ArrayList<Router> attackRouters = attack.getRouters();
		for (Router router : attackRouters) {
			for (int i = 0; i < attack.getSteps() * attack.getInterval(); i = i + attack.getInterval()) {
				for (int j = 0; j < attack.getNumber(); j++) {
					Simulation.getEventManager().addPacketTransmission(attack.getRule(), router, attack.getBegin() + i);
				}
			}
		}
	}
	
	private void generateTraffic(Router router, int begin, int interval, int steps, int number) {
		Set<Rule> routerRules = router.shareRoutes().keySet();
//		System.out.println("Router rules count = " + routerRules.size());
		ArrayList<Rule> routerRulesList = new ArrayList<Rule>(routerRules);
		for (int i = 0; i < steps * interval; i = i + interval) {
			int ruleIndex = (int) (Math.random() * routerRulesList.size());
			for (int j = 0; j < number; j++) {
				Simulation.getEventManager().addPacketTransmission(routerRulesList.get(ruleIndex), router, begin + i);
			}
		}
	}
	
	public void generateTraffic(int ringNumber, int numberRouters, int begin, int interval, int steps, int number) {
		RouterSet ring = rings.get(ringNumber - 1);
		ArrayList<Router> trafficRouters = new ArrayList<Router>(ring.getRouterSet());
		while (trafficRouters.size() > numberRouters) {
			trafficRouters.remove(Math.random() * trafficRouters.size());
		}
		
		for (Router trafficRouter : trafficRouters) {
			generateTraffic(trafficRouter, begin, interval, steps, number);
		}
	}
	
	public boolean checkAttacksAtAllHosts() {
		System.out.println("Checking attacks at all hosts...");
		boolean attackDetected = false;
		Iterator<Host> hostsIterator = hosts.iterator();
		while (hostsIterator.hasNext()) {
			boolean thisHostAttacked = hostsIterator.next().checkAttack(); 
			attackDetected |= thisHostAttacked;
		}
		
		return attackDetected;
	}
	
	public JSONObject getTopologyMap() {
		
		JSONObject json = new JSONObject();
		
		JSONArray nodes = new JSONArray();
		JSONArray links = new JSONArray();
		
		try {
			for (Host host : hosts) {
				JSONObject hostObject = new JSONObject();
				hostObject.put("id", host.getName());
				hostObject.put("name", host.getName());
				hostObject.put("capacity", host.getCapacity());
				hostObject.put("type", 1);
				nodes.put(hostObject);
			}
			
			for (Router router : mAllRouters.getRouterSet()) {
				JSONObject routerObject = new JSONObject();
				routerObject.put("id", router.getName());
				routerObject.put("name", router.getName());
				routerObject.put("ring", router.getRing());
				routerObject.put("type", 2);
				nodes.put(routerObject);
				
				for (Node successor : router.getSuccessors()) {
					JSONObject link = new JSONObject();
					link.put("source", router.getName());
					link.put("target", successor.getName());
					links.put(link);
				}
			}
			
			json.put("nodes", nodes);
			json.put("links", links);
			
		} catch (JSONException e) {}
		
		
		
		return json;
	}
	
	public void displayRoutingTables() {
		
		for (RouterSet ring : rings) {
			List<Router> ringRouters = ring.getRouterSet();
			for (Router router : ringRouters) {
				router.displayRoutingTable();
			}
		}
		
	}
	
	public void displaySuccessorsForAllRouters() {
		
		for (RouterSet ring : rings) {
			List<Router> ringRouters = ring.getRouterSet();
			for (Router router : ringRouters) {
				router.displaySuccessors();
			}
		}
		
	}
	
	public ArrayList<Rule> getRules() {
		return rules;
	}
	
	public ArrayList<RouterSet> getRings() {
		return rings;
	}
	
	public RouterSet getAllRouters() {
		return mAllRouters;
	}
	
	public ArrayList<Host> getHosts() {
		return hosts;
	}
	
}
