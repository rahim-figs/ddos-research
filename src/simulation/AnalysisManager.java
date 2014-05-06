package simulation;
import java.util.ArrayList;
import java.util.HashMap;


public class AnalysisManager {

	private Simulation Simulation;
	private ArrayList<Attack> mAttacks;
	private ArrayList<Detection> mDetections;
	private ArrayList<HashMap<RouterSet, Float>> mFalsePositiveStats;
	private ArrayList<HashMap<RouterSet, Float>> mDetectionRateStats;
	private ArrayList<HashMap<RouterSet, Float>> mTimeDetectionStats;
	private ArrayList<Float> mIPSEffectivenessRate;
	private ArrayList<Integer> mAttacksDetectedCount;
	private ArrayList<RouterSet> mRouterSets;
	
	public AnalysisManager(Simulation simulation) {
		Simulation = simulation;
		mAttacks = new ArrayList<Attack>();
		mDetections = new ArrayList<Detection>();
		mFalsePositiveStats = new ArrayList<HashMap<RouterSet, Float>>();
		mDetectionRateStats = new ArrayList<HashMap<RouterSet, Float>>();
		mTimeDetectionStats = new ArrayList<HashMap<RouterSet, Float>>();
		mAttacksDetectedCount = new ArrayList<Integer>();
		mIPSEffectivenessRate = new ArrayList<Float>();
		mRouterSets = new ArrayList<RouterSet>();
	}
	
	public void reset() {
		mAttacks.clear();
		mDetections.clear();
	}
	
	public void addDetection(Detection detection) {
		mDetections.add(detection);
	}
	
	public void addRouterSet(ArrayList<RouterSet> rings) {
		for (RouterSet ring : rings) {
			addRouterSet(ring);
		}
	}
	
	public void addRouterSet(RouterSet routerSet) {
		mRouterSets.add(routerSet);
	}
	
	public void addAttack(Attack attack) {
		mAttacks.add(attack);
	}
	
	public Simulation getSimulation() {
		return Simulation;
	}
	
	public Attack testDetectionAttack(Detection detection) {
		
		for (Attack attack : mAttacks) {
//			if (attack.getReachability().containsKey(detection.getRouter())) {
//				int reachCost = attack.getReachability().get(detection.getRouter()).intValue();
//				
//				if ((attack.getBegin() + reachCost <= detection.getTime())
//						&& (attack.getBegin() + (attack.getSteps() - 1) * attack.getInterval() + reachCost >= detection.getTime())) {
//					return attack;
//				}
//			}
			
			if (attack.getRule().equals(detection.getRule())) {
				return attack;
			}
		}
		
		return null;
	}
	
	public void computeRates(int simulationNumber) {
		
		HashMap<RouterSet, Float> falsePositiveMap = new HashMap<RouterSet, Float>();
		HashMap<RouterSet, Float> detectionMap = new HashMap<RouterSet, Float>();
		HashMap<RouterSet, Float> detectionTimeMap = new HashMap<RouterSet, Float>();
		
		for (RouterSet routerSet : mRouterSets) {
			
			ArrayList<Attack> attacksToDetect = new ArrayList<Attack>();
			ArrayList<Attack> attacksToDetectCopy = new ArrayList<Attack>();
			
			for (Router router : routerSet.getRouterSet()) {
				for (Attack attack : mAttacks) {
					if (attack.getReachability().containsKey(router) && (!attacksToDetect.contains(attack))) {
						attacksToDetect.add(attack);
					}
				}
			}
			
			attacksToDetectCopy.addAll(attacksToDetect);
			
			int detectedCount = 0, falsePositiveCount = 0;
			
			HashMap<Attack, Integer> attackDetectionTimeMap = new HashMap<Attack, Integer>();
			for (Detection detection : mDetections) {
				Attack attack = testDetectionAttack(detection);
				if (attack != null) {
					attack.setDetected();
					int delta = Math.abs(detection.getTime() - (attack.getBegin() + attack.getReachability().get(detection.getRouter())));
					if (attacksToDetectCopy.contains(attack)) {
						attackDetectionTimeMap.put(attack, delta);
						detectedCount++;
						
						attacksToDetectCopy.remove(attack);
					} else {
						attackDetectionTimeMap.put(attack, Math.min(attackDetectionTimeMap.get(attack), delta));
					}
				} else {
					falsePositiveCount++;
				}
			}
			
			System.out.println("Detection count = " + detectedCount);
			System.out.println("False positive count = " + falsePositiveCount);
			System.out.println("All detections count = " + mDetections.size());
			
			if (attacksToDetect.size() > 0) {
				detectionMap.put(routerSet, (float) detectedCount * 1.0f / attacksToDetect.size());
			} else {
				detectionMap.put(routerSet, 1f);
			}
			
			falsePositiveMap.put(routerSet, (float) falsePositiveCount);
			
			float average = 0;
			for (int detectedTime : attackDetectionTimeMap.values()) {
				average += detectedTime;
			}
			
			if (attackDetectionTimeMap.size() > 0) {
				average /= attackDetectionTimeMap.size();
			}
			
			detectionTimeMap.put(routerSet, average);
		}
		
		// Calculate IPS effectiveness rate
		float ipsEffectivenessRate = 0;
		int detectedCount = 0;
		for (Attack attack : mAttacks) {
			
			if (attack.isDetected()) {
				detectedCount++;
			}
			
			int reachableRoutersCount = attack.getReachability().size();
			HashMap<Router, Boolean> routersWhichDetectedThisAttack = new HashMap<Router, Boolean>();
			for (Detection detection : mDetections) {
				Attack matchedAttack = testDetectionAttack(detection);
				if (attack == matchedAttack) {
					routersWhichDetectedThisAttack.put(detection.getRouter(), true);
				}
			}
			int routersWhichDetectedThisAttackCount = routersWhichDetectedThisAttack.size();
			
			ipsEffectivenessRate += (float) routersWhichDetectedThisAttackCount / reachableRoutersCount;
		}
		
		
		ipsEffectivenessRate /= mAttacks.size();
		mAttacksDetectedCount.add(simulationNumber, detectedCount);
		mIPSEffectivenessRate.add(simulationNumber, ipsEffectivenessRate);
		
		mTimeDetectionStats.add(simulationNumber, detectionTimeMap);
		mDetectionRateStats.add(simulationNumber, detectionMap);
		mFalsePositiveStats.add(simulationNumber, falsePositiveMap);
		
		
	}
	
	public void displayIPSEffectivenessRate() {
		System.out.println("IPS effectiveness rates:");
		
		for (float ipsEffectivenessRate : mIPSEffectivenessRate) {
			System.out.print(ipsEffectivenessRate + "; ");
		}
		
		System.out.println();
	}
	
	public void displayDetectionRate() {
		
		System.out.println("Detection rates:");
		
		int simulationNumber = 1;
		for (HashMap<RouterSet, Float> detectionRateStat : mDetectionRateStats) {
			System.out.println("Simulation number: " + simulationNumber);
			
			for (RouterSet routerSet : detectionRateStat.keySet()) {
				System.out.print(" Routerset: " + routerSet.getName() + "; Rate = " + detectionRateStat.get(routerSet).floatValue());
			}
			
			System.out.println();
			
			simulationNumber++;
		}
		
	}
	
	public void displayDetectionTime() {
		
		System.out.println("Detection times:");
		
		int simulationNumber = 1;
		for (HashMap<RouterSet, Float> timeDetectionStat : mTimeDetectionStats) {
			System.out.println("Simulation number: " + simulationNumber);
			
			for (RouterSet routerSet : timeDetectionStat.keySet()) {
				System.out.print(" Routerset: " + routerSet.getName() + "; Rate = " + timeDetectionStat.get(routerSet).floatValue());
			}
			
			System.out.println();
			
			simulationNumber++;
		}
		
	}
	
	public void displayFalsePositives() {
		
		System.out.println("False positives:");
		
		int simulationNumber = 1;
		for (HashMap<RouterSet, Float> falsePositiveStat : mFalsePositiveStats) {
			System.out.println("Simulation number: " + simulationNumber);
			
			for (RouterSet routerSet : falsePositiveStat.keySet()) {
				System.out.print(" Routerset: " + routerSet.getName() + "; Rate = " + falsePositiveStat.get(routerSet).floatValue());
			}
			
			System.out.println();
			
			simulationNumber++;
		}
		
	}
	
	public void displayAverageDetectionRateX(int averageWindowSize) {
		
		System.out.println("Average detection rate");
		int k = 0;
		int j = 0;
		
		for (RouterSet routerSet : mRouterSets) {
			k++;
			float average = 0;
			System.out.print(routerSet.getName());
			for (HashMap<RouterSet, Float> detectionRateStat : mDetectionRateStats) {
				average += detectionRateStat.get(routerSet).floatValue();
				j++;
			}
			
			if (k >= averageWindowSize) {
				average /= j;
				System.out.print(";" + average);
				k = 0;
				j = 0;
				average = 0;
			}
			
			System.out.println();
		}
	}
	
	public void displayAverageFalsePositiveX(int averageWindowSize) {
		
		System.out.println("Average false positive");
		int k = 0;
		int j = 0;
		
		for (RouterSet routerSet : mRouterSets) {
			k++;
			float average = 0;
			System.out.print(routerSet.getName());
			for (HashMap<RouterSet, Float> falsePositiveStat : mFalsePositiveStats) {
				average += falsePositiveStat.get(routerSet).floatValue();
				j++;
			}
			
			if (k >= averageWindowSize) {
				average /= j;
				System.out.print(";" + average);
				k = 0;
				j = 0;
				average = 0;
			}
			
			System.out.println();
		}
	}
	
	public void displayAverageFalsePositive(int averageWindowSize) {
		
		System.out.println("Average false positive");
		
		int c = 0;
		int subCount = 0;
		float average = 0;
		for (HashMap<RouterSet, Float> falsePositiveStat : mFalsePositiveStats) {
			for (RouterSet routerSet : falsePositiveStat.keySet()) {
				subCount++;
				average += falsePositiveStat.get(routerSet).floatValue();
			}
			
			if (++c >= averageWindowSize) {
				average /= subCount;
				c = 0;
				subCount = 0;
				System.out.println("" + average + ";");
			}
		}
		
		System.out.println();
	}
	
	public void displayAverageDetectionRate(int averageWindowSize) {
		
		System.out.println("Average detection rate");
		
		int c = 0;
		int subCount = 0;
		float average = 0;
		for (HashMap<RouterSet, Float> detectionRateStat : mDetectionRateStats) {
			for (RouterSet routerSet : detectionRateStat.keySet()) {
				subCount++;
				average += detectionRateStat.get(routerSet).floatValue();
			}
			
			if (++c >= averageWindowSize) {
				average /= subCount;
				c = 0;
				subCount = 0;
				System.out.println("" + average + ";");
			}
		}
		
		System.out.println();
	}
	
	public float[] getFalsePositiveSet(int simulationsPerParameter) {
		
		float[] falsePositives = new float[9];
		
		int c = 0;
		int subCount = 0;
		float average = 0;
		int i = 0;
		for (HashMap<RouterSet, Float> falsePositiveStat : mFalsePositiveStats) {
			for (RouterSet routerSet : falsePositiveStat.keySet()) {
				subCount++;
				average += falsePositiveStat.get(routerSet).floatValue();
			}
			
			if (++c >= simulationsPerParameter) {
				average /= subCount;
				falsePositives[i++] = average;
				c = 0;
				subCount = 0;
			}
		}
		
		return falsePositives;
	}
	
	public float[] getIPSEffectivenessSet() {
		
		float[] ipsEffectivenessArray = new float[9];
		float average = 0;
		int c = 0;
		int w = mIPSEffectivenessRate.size() / 9;
		for (int i = 0; i < mIPSEffectivenessRate.size(); i++) {
			
			if (i > 0 && i % w == 0) {
				average /= c;
				ipsEffectivenessArray[(i - 1) / w] = average;
				average = 0;
				c = 0;
			}
			
			average += mIPSEffectivenessRate.get(i);
			c++;
			
		}
		
		if (average > 0 && c > 0) {
			average /= c;
			ipsEffectivenessArray[8] = average;
		}
		
		return ipsEffectivenessArray;
	}
	
	public int[] getAttacksDetectedCountSet() {
		
		int[] attacksDetectedCount = new int[9];
		int count = 0;
		int c = 0;
		int w = mIPSEffectivenessRate.size() / 9;
		System.out.println("w = " + w);
		for (int i = 0; i < mAttacksDetectedCount.size(); i++) {
			
			if (i > 0 && i % w == 0) {
				count /= c;
				attacksDetectedCount[(i - 1) / w] = count;
				System.out.println("saved count = " + count);
				System.out.println("Saving at " + ((i - 1) / w));
				count = 0;
				c = 0;
			}
			
			System.out.println("attacks " + mAttacksDetectedCount.get(i));
			count += mAttacksDetectedCount.get(i);
			System.out.println("count = " + count);
			c++;
			
		}
		
		if (count > 0 && c > 0) {
			count /= c;
			attacksDetectedCount[8] = count;
		}
		
		return attacksDetectedCount;
	}
	
	public void displayAverageDetectionTime(int averageWindowSize) {
		
		System.out.println("Average detection time");
		int k = 0;
		int j = 0;
		
		for (RouterSet routerSet : mRouterSets) {
			k++;
			float average = 0;
			System.out.print(routerSet.getName());
			for (HashMap<RouterSet, Float> detectionTimeStat : mTimeDetectionStats) {
				average += detectionTimeStat.get(routerSet).floatValue();
				j++;
			}
			
			if (k >= averageWindowSize) {
				average /= j;
				System.out.print(";" + average);
				k = 0;
				j = 0;
				average = 0;
			}
			
			System.out.println();
		}
	}
	
	public void displayAttacks() {
		
		System.out.println("Displaying attacks");
		
		for (Attack attack : mAttacks) {
			System.out.println("" + attack.getRule().getRuleString() + "; " + attack.getRouters().size());
		}
		
		System.out.println();
	}
}
