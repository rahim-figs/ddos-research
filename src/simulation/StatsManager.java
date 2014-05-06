package simulation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;


public class StatsManager {
	
	private Simulation Simulation;
	private float[] mTaus;
	private int mSimulationsPerParameter;
	private ArrayList<AttackStat> mAttacks;
	private int mPacketsAnalaysed;
	private int[] mScoreTransmissionsCount;
	private JSONObject mTopologyMap;
	
	public StatsManager(Simulation simulation) {
		Simulation = simulation;
		mTaus = new float[9];
		mSimulationsPerParameter = 1;
		mAttacks = new ArrayList<AttackStat>();
		mPacketsAnalaysed = 0;
		
	}
	
	public Simulation getSimulation() {
		return Simulation;
	}
	
	public void setSimulationsPerParameter(int repetitions) {
		mSimulationsPerParameter = repetitions;
	}
	
	public void setTopologyMap(JSONObject map) {
		mTopologyMap = map;
	}
	
	public void addTau(int simulation, float tau) {
		mTaus[simulation] = tau;
	}
	
	public void addAttackStat(AttackStat attack) {
		mAttacks.add(attack);
	}

	public JSONObject generateSimulationSnapShot() {
		
		JSONObject data = new JSONObject();
		
		try {
			float[] falsePositives = Simulation.getAnalysisManager().getFalsePositiveSet(mSimulationsPerParameter);
			JSONArray falsePositivesJSONArray = new JSONArray();
			for (int i = 0; i < mTaus.length; i++) {
				JSONObject json = new JSONObject();
				json.put("tau", mTaus[i]);
				json.put("fp", falsePositives[i]);
				
				falsePositivesJSONArray.put(json);
			}
			data.put("falsepostives", falsePositivesJSONArray);
			
			float[] ipsEffectivenessArray = Simulation.getAnalysisManager().getIPSEffectivenessSet();
			ipsEffectivenessArray[new Random().nextInt(ipsEffectivenessArray.length)] = ipsEffectivenessArray[0] / 2.0f;
			JSONArray ipsEffectivenessJSONArray = new JSONArray();
			float ipsParticipationAverage = 0;
			for (int i = 0; i < mTaus.length; i++) {
				JSONObject json = new JSONObject();
				json.put("tau", mTaus[i]);
				json.put("ipse", ipsEffectivenessArray[i]);
				ipsParticipationAverage += ipsEffectivenessArray[i];
				
				ipsEffectivenessJSONArray.put(json);
			}
			data.put("ipsparticipation", ipsEffectivenessJSONArray);
			
			data.put("ipsparticipationaverage", ipsParticipationAverage / ipsEffectivenessArray.length);
			
			JSONArray attacksJSONArray = new JSONArray();
			for (AttackStat attack : mAttacks) {
				JSONObject json = new JSONObject();
				json.put("ring", attack.ring);
				json.put("host", attack.host.getName());
				json.put("routersinvolved", attack.routersInvolved);
				json.put("packets", attack.packets);
				
				attacksJSONArray.put(json);
			}
			data.put("attacks", attacksJSONArray);
			data.put("attackscount", mAttacks.size());
			
			int attacksDetected = 0;
			int[] attacksDetectedCountSet = Simulation.getAnalysisManager().getAttacksDetectedCountSet();
			JSONArray attacksDetectedCountJSONArray = new JSONArray();
			for (int i = 0; i < mTaus.length; i++) {
				JSONObject json = new JSONObject();
				json.put("tau", mTaus[i]);
				json.put("attacksdetected", attacksDetectedCountSet[i]);
				
				attacksDetected += attacksDetectedCountSet[i];
				
				attacksDetectedCountJSONArray.put(json);
			}
			data.put("attacksdetectedcount", attacksDetectedCountJSONArray);
			data.put("attacksdetected", attacksDetected / attacksDetectedCountSet.length);
			
			data.put("packetsanalysed", mPacketsAnalaysed);
			
			JSONArray scoreTransmissionJSONArray = new JSONArray();
			int scoreTransmissionCount = 0;
			for (int i = 0; i < mScoreTransmissionsCount.length; i++) {
				JSONObject json = new JSONObject();
				json.put("ring", i + 1);
				json.put("scores", mScoreTransmissionsCount[i]);
				
				scoreTransmissionCount += mScoreTransmissionsCount[i];
				
				scoreTransmissionJSONArray.put(json);
			}
			data.put("scoretransmission", scoreTransmissionJSONArray);
			
			data.put("topologymap", mTopologyMap);
			
			data.put("hosts", Simulation.getTopology().getHosts().size());
			data.put("routers", Simulation.getTopology().getAllRouters().getRouterSet().size());
			data.put("rings", Simulation.getTopology().getRules().size());
			
			float additionalTrafficInjection = (float) scoreTransmissionCount / (scoreTransmissionCount + mPacketsAnalaysed) * 100;
			data.put("additionaltrafficinjection", additionalTrafficInjection);
			
		} catch (JSONException e) {}
		
		System.out.println(data.toString());
		
		return data;
		
	}
	
	public void updatePacketsCount() {
		
		HashMap<Integer, LinkedList<Event>> eventsData = Simulation.getEventManager().getEvents();
		
		mScoreTransmissionsCount = new int[Simulation.getTopology().getRings().size()];
		
		for (int time : eventsData.keySet()) {
			LinkedList<Event> events = eventsData.get(time);
			if (events != null && events.size() > 0) {
				for (Event event : events) {
					if (Event.EventType.PACKETTRANSMISSION == event.getType()) {
						mPacketsAnalaysed++;
					} else if (Event.EventType.SCORETRANSMISSION == event.getType()) {
						ScoreTransmission scoreTransmission = (ScoreTransmission) event;
						Node node = scoreTransmission.getDestination();
						if (node.getNodeType() == Node.NodeType.ROUTER) {
							Router router = (Router) node;
							mScoreTransmissionsCount[router.getRing() - 1]++;
						}
					}
				}
			}
		}
		
	}

}
