package simulation;
import java.util.ArrayList;

import com.google.appengine.labs.repackaged.org.json.JSONObject;


public class Driver {
	
	// Block initialization
	private Driver() {
		
	}
	
	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		test1();
		long endTime = System.currentTimeMillis();
		
		System.out.println("Simulation completed in " + ((endTime - startTime) / 1000) + " seconds");
		System.out.println("***********************");
		
//		DempsterShaferTest.test();
		
	}
	
	static void test1() {
		
		Simulation simulation = new Simulation()
			.setGamma(0.4f)
			.setAlpha(0.8f)
			.setA(0.5f)
			.setB1(1f)
			.setB2(0.65f)
			.setB3(0.8f)
			.setEpsilon(0.01f)
			.setOmega(0.05f)
			.setBeta(0.4f)
			.setTau(0.9f)
			.setUpsilon(0.05f)
			.init();
		
		Topology topology = simulation.getTopology();
		topology.initHosts(4, 200, 1);
		topology.initRouters(3, 0.3, 3, 1);
		topology.computeRoutes();
		
		JSONObject topologyGraph = topology.getTopologyMap();
		
		System.out.println(topologyGraph.toString());
		
		topology.displayRoutingTables();
		topology.displaySuccessorsForAllRouters();
		
		simulation.getAnalysisManager().addRouterSet(topology.getAllRouters());
		simulation.getAnalysisManager().addRouterSet(topology.getRings());
		
//		System.out.println("Generating attacks");
		ArrayList<Attack> attacks = simulation.addRandomAttacks(2);
		
		
//		Rule attackRule1 = topology.getRules().get(0);
//		
//		Attack attack1 = topology.createAttack(
//				3, // ring 
//				10, // number of routers
//				attackRule1, // rule
//				10, // begin
//				5, // interval
//				10, // steps
//				50 // number of packets
//			);
//		attack1.computeReachability();
//		simulation.getAnalysisManager().addAttack(attack1);
//		
//		Rule attackRule2 = topology.getRules().get(2);
//		Attack attack2 = topology.createAttack(
//				3, // ring 
//				10, // number of routers
//				attackRule2, // rule
//				10, // begin
//				5, // interval
//				10, // steps
//				50 // number of packets
//			);
//		attack2.computeReachability();
//		simulation.getAnalysisManager().addAttack(attack2);
		
		simulation.getAnalysisManager().displayAttacks();
//		System.out.println("Before playing...");
//		simulation.getEventManager().displayAllEvents();
		
		float[] taus = { 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f };
		for (int i = 0; i < taus.length; i++) {
			simulation.getStatsManager().addTau(i, taus[i]);
		}
		
		for (int i = 0; i < 9; i++) {
			
			System.out.println("########################################    " + i);
			
			simulation.setTau(taus[i/1]);
			
			simulation.getEventManager().reset();
			simulation.getAnalysisManager().reset();
			
			for (Attack attack : attacks) {
				simulation.getAnalysisManager().addAttack(attack);
				topology.generateAttack(attack);
			}
			
			topology.generateTraffic(
					3, // ring
					5, // number of routers
					1, // begin
					5, // interval
					10, // steps
					10 // number of packets
				);
			
			simulation.getEventManager().play();
			simulation.getAnalysisManager().computeRates(i);
			
			simulation.getStatsManager().updatePacketsCount();
			
		}
		
		simulation.getAnalysisManager().displayDetectionRate();
		simulation.getAnalysisManager().displayFalsePositives();
		
		simulation.getAnalysisManager().displayAverageDetectionRate(1);
		simulation.getAnalysisManager().displayAverageFalsePositive(1);
		
		simulation.getAnalysisManager().displayIPSEffectivenessRate();
		
		simulation.getStatsManager().generateSimulationSnapShot();
		
	}

}