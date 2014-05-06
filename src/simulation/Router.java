package simulation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Router extends Node {

	private int ring;
	private int routerNumber;
	private String name;
	private ArrayList<Node> successors;
	
	private HashMap<Rule, Hop> mRoutingTable;
	private HashMap<Rule, Integer> mRuleCount;
	private HashMap<Rule, Float> mRuleFrequencyMap;
	private HashMap<Rule, Float> mScoreMap;
	private HashMap<Rule, Float> mProfileMap;
	private ArrayList<HashMap<Rule, Float>> mIPSExchange;
	
	private static Rule sUncertainRule = new Rule(-1);
	
	public Router(int ring, int routerNumber, int windowLength, Topology topology) {
		super(NodeType.ROUTER, windowLength, topology);
		
		this.ring = ring;
		this.routerNumber = routerNumber;
		
		this.name = "R-" + this.ring + "-" + this.routerNumber;
		
		this.successors = new ArrayList<Node>();
		this.mRoutingTable = new HashMap<Rule, Hop>();
		this.mRuleCount = new HashMap<Rule, Integer>();
		this.mRuleFrequencyMap = new HashMap<Rule, Float>();
		this.mScoreMap = new HashMap<Rule, Float>();
		this.mProfileMap = new HashMap<Rule, Float>();
		this.mIPSExchange = new ArrayList<HashMap<Rule, Float>>();
		this.Topology = topology;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void addSuccessor(Node node) {
		successors.add(node);
	}
	
	public ArrayList<Node> getSuccessors() {
		return successors;
	}
	
	public int getRing() {
		return ring;
	}
	
	public void addRoute(Rule rule, Node nextNode, int time) {
		Hop hop = mRoutingTable.get(rule);
		if (hop == null || hop.time > time) {
			mRoutingTable.put(rule, new Hop(nextNode, time));
		}
		mScoreMap.put(rule, 0f);
		mProfileMap.put(rule, 0f);
	}
	
	public HashMap<Rule, Hop> shareRoutes() {
		return mRoutingTable;
	}
	
	@Override
	public void receivePacket(Rule rule, int size) {
		
		int ruleReceivedCount = mRuleCount.get(rule) != null ? mRuleCount.get(rule).intValue() : 0;
		mRuleCount.put(rule, ruleReceivedCount + size);
		
		//System.out.println("----------------> Received packet at " + getName() + " rulecount = " + (ruleReceivedCount + size) + " mapsize = " + mRuleCount.keySet().size());
		
		for (Node successor : successors) {
			if (successor.getNodeType() == Node.NodeType.HOST) {
				Host destinationHost = (Host) successor;
				if (rule.equals(destinationHost.getRule())) {
					Topology.getSimulation().getEventManager().addPacketTransmissionAtCurrentTime(rule, successor);
				}
			} else {
				Topology.getSimulation().getEventManager().addPacketTransmissionAtCurrentTime(rule, successor);
			}
		}
		
		super.receivePacket(rule, size);
	}
	
	@Override
	public void receiveScores(HashMap<Rule, Float> scoresMap) {
		super.receiveScores(scoresMap);
		
//		System.out.println("----------------> Received score at " + getName());
		
		mIPSExchange.add(scoresMap);
	}
	
	@Override
	public void endWindow() {
		
//		System.out.println("Window ended for " + getName());
		computeFrequencies();
		
		// displayFrequencies();
		
		preUpdateScores();
		
//		displayScores();
		
		if (firstCheck()) {
//			System.out.println("First check.. .potential ....");
			updateScores(rulesSelection());
			detect();
		} else {
//			System.out.println("First check.. .harmless ....");
		}
		
		Iterator<Rule> rulesIterator = mRuleFrequencyMap.keySet().iterator();
		while (rulesIterator.hasNext()) {
			Rule rule = rulesIterator.next();
			if (mProfileMap.get(rule).floatValue() < 0.00000001) {
				mProfileMap.put(rule, mRuleFrequencyMap.get(rule).floatValue());
//				System.out.println("Profile initiated..." + mRuleFrequencyMap.get(rule).floatValue());
			} else {
				mProfileMap.put(rule, mProfileMap.get(rule).floatValue() * Topology.getSimulation().getA() 
							+ (1 - Topology.getSimulation().getA()) * mRuleFrequencyMap.get(rule).floatValue());
//				System.out.println("Profile updated..." + mRuleFrequencyMap.get(rule).floatValue());
			}
		}
		
		super.endWindow();
	}
	
	private void computeFrequencies() {
		
//		System.out.println("computing freq " + mPacketsCount);
		if (mPacketsCount > 0) {
			Iterator<Rule> rulesIterator = mRuleCount.keySet().iterator();
			
			while (rulesIterator.hasNext()) {
				Rule rule = rulesIterator.next();
				mRuleFrequencyMap.put(rule, new Float((float) (mRuleCount.get(rule) / mPacketsCount)));
				mRuleCount.put(rule, 0);
			}
		}
	}
	
	private void preUpdateScores() {
		
		float total = 0;
		mScoreMap.remove(sUncertainRule);
		Iterator<Rule> scoresIterator = mScoreMap.keySet().iterator();
		ArrayList<Rule> toRemove = new ArrayList<Rule>();
		while (scoresIterator.hasNext()) {
			Rule rule = scoresIterator.next();
			float score = Topology.getSimulation().getA() * mScoreMap.get(rule);
			
			if (score > Topology.getSimulation().getUpsilon()) {
				mScoreMap.put(rule, score);
				total += score;
			} else {
				toRemove.add(rule);
			}
		}
		
		for (Rule ruleToRemove : toRemove) {
			mScoreMap.remove(ruleToRemove);
		}
		
		mScoreMap.put(sUncertainRule, 1 - total);
		
	}
	
	private boolean firstCheck() {
		
		float entropy = 0;
		Iterator<Rule> rulesIterator = mRuleFrequencyMap.keySet().iterator();
		while (rulesIterator.hasNext()) {
			Rule rule = rulesIterator.next();
			
			if (mRuleFrequencyMap.get(rule).floatValue() != 0
					&& mProfileMap.get(rule) != null
					&& mProfileMap.get(rule).floatValue() != 0) {
				
//				System.out.println("calculating entropy = " + mRuleFrequencyMap.get(rule).floatValue() + " : " + mProfileMap.get(rule).floatValue() + " : " + mRuleFrequencyMap.keySet().size());
				
				entropy += (
						mRuleFrequencyMap.get(rule).floatValue()
						* log(
								mRuleFrequencyMap.get(rule).floatValue()
								/ mProfileMap.get(rule).floatValue(),
								mRuleFrequencyMap.keySet().size()
							)
						);
			}
		}
		
//		System.out.println("entropy = " + entropy);
		
		if (entropy > Topology.getSimulation().getOmega() || entropy * -1f > Topology.getSimulation().getOmega()) {
			return true;
		}
		
		return false;
	}
	
	private HashMap<Rule, Float> rulesSelection() {
//		displayFrequencies();
		float entropy = entropy();
//		System.out.println("Packes count = " + mPacketsCount);
//		System.out.println("Entropy = " + entropy);
		HashMap<Rule, Float> scores = new HashMap<Rule, Float>();
		float total = 0;
		if (entropy > Topology.getSimulation().getAlpha()) {
			// High entropy
			Iterator<Rule> frequenciesIterator = mRuleFrequencyMap.keySet().iterator();
			while (frequenciesIterator.hasNext()) {
				Rule rule = frequenciesIterator.next();
				if (mRuleFrequencyMap.get(rule).floatValue() > Topology.getSimulation().getEpsilon()
						&& mRuleFrequencyMap.get(rule).floatValue() / mProfileMap.get(rule).floatValue() > 1 + Topology.getSimulation().getGamma()) {
					
//					System.out.println("1");
					if (mRuleFrequencyMap.get(rule).floatValue() > Topology.getSimulation().getBeta()) {
//						System.out.println("2");
						// High frequency
						scores.put(rule, Topology.getSimulation().getB1() * mRuleFrequencyMap.get(rule).floatValue());
						total += (Topology.getSimulation().getB1() * mRuleFrequencyMap.get(rule).floatValue());
					} else {
//						System.out.println("3");
						// Low frequency
						scores.put(rule, Topology.getSimulation().getB2() * mRuleFrequencyMap.get(rule).floatValue());
						total += (Topology.getSimulation().getB2() * mRuleFrequencyMap.get(rule).floatValue());
					}
				}
			}
		} else {
			// Low entropy
			Iterator<Rule> frequenciesIterator = mRuleFrequencyMap.keySet().iterator();
			while (frequenciesIterator.hasNext()) {
				Rule rule = frequenciesIterator.next();
				if (mRuleFrequencyMap.get(rule).floatValue() > Topology.getSimulation().getEpsilon()
						&& mRuleFrequencyMap.get(rule).floatValue() / mProfileMap.get(rule).floatValue() > 1 + Topology.getSimulation().getGamma()) {
					
//					System.out.println("4");
					if (mRuleFrequencyMap.get(rule).floatValue() > Topology.getSimulation().getBeta()) {
						// High frequency
//						System.out.println("5");
						scores.put(rule, Topology.getSimulation().getB3() * mRuleFrequencyMap.get(rule).floatValue());
						total += (Topology.getSimulation().getB3() * mRuleFrequencyMap.get(rule).floatValue());
					}
				}
			}
		}
		
//		System.out.println("Total = " + total);
		
		scores.put(sUncertainRule, 1 - total);
		
		return scores;
	}
	
	private void detect() {
		HashMap<Rule, Float> scoreToSend = new HashMap<Rule, Float>();
		float total = 0;
		
		Iterator<Rule> rulesIterator = mScoreMap.keySet().iterator();
		while (rulesIterator.hasNext()) {
			Rule rule = rulesIterator.next();
			if (rule != sUncertainRule) {
//				System.out.println("" + mScoreMap.get(rule).floatValue() + " < " + Topology.getSimulation().getTau() + "?");
				if (mScoreMap.get(rule).floatValue() < Topology.getSimulation().getTau()) {
					scoreToSend.put(rule, mScoreMap.get(rule).floatValue());
					total = total + mScoreMap.get(rule).floatValue();
				} else {
					float maxScore = 1 * Topology.getSimulation().getA();
					float confidence = (mScoreMap.get(rule).floatValue() - Topology.getSimulation().getTau()) / maxScore;
					Topology.getSimulation().getEventManager().attackDetected(this, rule, confidence);
				}
			}
		}
		
		if (scoreToSend.size() > 0) {
			scoreToSend.put(sUncertainRule, 1 - total);
			
			if (successors.size() > 0) {
				Node node = successors.get(0);
				Topology.getSimulation().getEventManager().addScoreTransmission(scoreToSend, node);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateScores(HashMap<Rule, Float> newScores) {
		
//		System.out.println("old scores = ");
//		dumpScores(mScoreMap);
//		System.out.println("new scores = ");
//		dumpScores(newScores);
		
		ArrayList<Rule> rules = new ArrayList<Rule>();
		
		Iterator<Rule> oldScoresIterator = mScoreMap.keySet().iterator();
		while (oldScoresIterator.hasNext()) {
			Rule rule = oldScoresIterator.next();
			if (!rules.contains(rule)) {
				rules.add(rule);
			}
		}
		
		Iterator<Rule> newScoresIterator = newScores.keySet().iterator();
		while (newScoresIterator.hasNext()) {
			Rule rule = newScoresIterator.next();
			if (!rules.contains(rule)) {
				rules.add(rule);
			}
		}
		
		Iterator<HashMap<Rule, Float>> ipsExchangeIterator = mIPSExchange.iterator();
		while (ipsExchangeIterator.hasNext()) {
			HashMap<Rule, Float> scoreMap = ipsExchangeIterator.next();
			Iterator<Rule> scoresIterator = scoreMap.keySet().iterator();
			while (scoresIterator.hasNext()) {
				Rule rule = scoresIterator.next();
				if (!rules.contains(rule)) {
					rules.add(rule);
				}
			}
		}
		
		mIPSExchange.add(mScoreMap);
		mIPSExchange.add(newScores);
		
//		System.out.println("ips count = " + mIPSExchange.size());
		
		HashMap<Rule, Float> compoundScoreMap = new HashMap<Rule, Float>();
		float k = 1 - calculateK((ArrayList<HashMap<Rule, Float>>) mIPSExchange.clone(), new ArrayList<Rule>());
//		System.out.println("k = " + k);
		Iterator<Rule> rulesIterator = rules.iterator();
		while (rulesIterator.hasNext()) {
			Rule rule = rulesIterator.next();
			float belief = calculateBelief((ArrayList<HashMap<Rule, Float>>) mIPSExchange.clone(), rule, false);
//			System.out.println("belief = " + belief);
			compoundScoreMap.put(rule, belief / k);
		}
		
		mScoreMap = compoundScoreMap;
		
//		System.out.println("compound score map ");
//		dumpScores(compoundScoreMap);
		
		mIPSExchange.clear();
		
	}
	
	@SuppressWarnings("unchecked")
	private float calculateK(ArrayList<HashMap<Rule, Float>> toIterate, ArrayList<Rule> memory) {
		
		float result = 0f;
		
		if (toIterate.size() > 0) {
			
			HashMap<Rule, Float> scoreMap = toIterate.remove(0);
			Iterator<Rule> ruleIterator = scoreMap.keySet().iterator();
			while (ruleIterator.hasNext()) {
				Rule rule = ruleIterator.next();
				if (rule != sUncertainRule) {
				
					if (toIterate.size() > 0) {
						memory.add(rule);
						ArrayList<HashMap<Rule, Float>> temp = new ArrayList<HashMap<Rule, Float>>();
						temp.addAll(toIterate);
						
						result = result + (float) (scoreMap.get(rule).floatValue() * calculateK(temp, (ArrayList<Rule>) memory.clone()));
						
						memory.remove(rule);
						
					} else {
						if (checkList(memory)) {
							result = result + scoreMap.get(rule).floatValue();
						} else if (rule != memory.get(0)) {
							result = result + scoreMap.get(rule).floatValue();
						}
					}
				}
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private float calculateBelief(ArrayList<HashMap<Rule, Float>> toIterate, Rule memoryRule, boolean ok) {
		
		float result = 0;
		
		if (toIterate.size() > 0) {
			
			HashMap<Rule, Float> scoreMap = toIterate.remove(0);
			Iterator<Rule> ruleIterator = scoreMap.keySet().iterator();
			while (ruleIterator.hasNext()) {
				Rule rule = ruleIterator.next();
				if (rule == memoryRule) {
					if (toIterate.size() > 0) {
						result = result + (float) scoreMap.get(rule).floatValue() * calculateBelief((ArrayList<HashMap<Rule, Float>>) toIterate.clone(), memoryRule, true);
					} else {
						result = result + scoreMap.get(rule).floatValue();
					}
				} else if (rule == sUncertainRule) {
					if (toIterate.size() > 0) {
						result = result + (float) scoreMap.get(rule).floatValue() * calculateBelief((ArrayList<HashMap<Rule, Float>>) toIterate.clone(), memoryRule, ok);
					} else if (ok) {
						result = result + scoreMap.get(rule).floatValue();
					}
				}
			}
		}
		
		return result;
	}
	
	private boolean checkList(ArrayList<Rule> list) {
		if (list.size() > 1) {
			for (int i = 1; i < list.size(); i++) {
				if (list.get(i) != list.get(0)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
	
	private float entropy() {
		
		float entropy = 0;
		Iterator<Rule> rulesIterator = mRuleFrequencyMap.keySet().iterator();
		while (rulesIterator.hasNext()) {
			Rule rule = rulesIterator.next();
			
			if (mRuleFrequencyMap.get(rule).floatValue() != 0
					&& mProfileMap.get(rule) != null
					&& mProfileMap.get(rule).floatValue() != 0) {
				
				entropy += (
						mRuleFrequencyMap.get(rule).floatValue()
						* log(
								mRuleFrequencyMap.get(rule).floatValue()
								/ mProfileMap.get(rule).floatValue(),
								mRuleFrequencyMap.keySet().size()
							)
						);
			}
		}
		
		return entropy;
	}
	
	public int getTimeRoute(Node node) {
		
		for (Hop hop : mRoutingTable.values()) {
			if (hop.node == node) {
				return hop.time;
			}
		}
		
		return 1;
	}
	
	public void displayRoutingTable() {
		
		System.out.println("Routing table for router " + getName());
		
		for (Rule rule : mRoutingTable.keySet()) {
			System.out.println(rule.getRuleString() + " : " + mRoutingTable.get(rule).node.getName() + " : " + mRoutingTable.get(rule).time);
		}
		
		System.out.println();
	}
	
	public void displaySuccessors() {
		
		System.out.println("Successors for router " + getName());
		
		for (Node successor : successors) {
			System.out.println(successor.getName());
		}
		
		System.out.println();
	}
	
	public void displayScores() {
		System.out.println("Scores for router " + getName());
		
		for (Rule rule : mScoreMap.keySet()) {
			System.out.println("Rule = " + rule.getRuleString() + ", score = " + mScoreMap.get(rule).floatValue());
		}
		
		System.out.println();
	}
	
	public void displayFrequencies() {
		System.out.println("Frequencies for router " + getName());
		
		for (Rule rule : mRuleFrequencyMap.keySet()) {
			System.out.println("Rule = " + rule.getRuleString() + ", score = " + mRuleFrequencyMap.get(rule).floatValue());
		}
		
		System.out.println();
	}
	
	private void dumpScores(HashMap<Rule, Float> scores) {
		
		for (Rule rule : scores.keySet()) {
			System.out.println("" + rule.getRuleString() + " : " + scores.get(rule).floatValue());
		}
	}
	
	private float log(float val, int base) {
		return ((float) (Math.log(val)/Math.log(base)));
	}
}
