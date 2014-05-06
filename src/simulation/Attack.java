package simulation;
import java.util.ArrayList;
import java.util.HashMap;


public class Attack {

	private ArrayList<Router> mRouters;
	private Rule mRule;
	private int mBegin;
	private int mInterval;
	private int mSteps;
	private int mNumber;
	private HashMap<Node, Integer> mReachability;
	private boolean mDetected;
	
	public Attack(ArrayList<Router> routers, Rule rule, int begin, int interval, int steps, int number) {
		mRouters = routers;
		mRule = rule;
		mBegin = begin;
		mInterval = interval;
		mSteps = steps;
		mNumber = number;
		mReachability = new HashMap<Node, Integer>();
		mDetected = false;
	}
	
	public void setDetected() {
		mDetected = true;
	}
	
	public boolean isDetected() {
		return mDetected;
	}
	
	public void computeReachability() {
		
		for (Router router : mRouters) {
			computeReachability(router, 0);
		}
		
		displayReachability();
	}
	
	private void computeReachability(Node node, int level) {
		
		mReachability.put(node, level);
		
		for (Node successor : node.getSuccessors()) {
			computeReachability(successor, level + node.getTimeRoute(successor));
		}
	}
	
	public void displayReachability() {
		
		System.out.println("Displaying reachability...");
		
		for (Node node : mReachability.keySet()) {
			System.out.println("" + node.getName() + " : " + mReachability.get(node).intValue());
		}
	}
	
	public HashMap<Node, Integer> getReachability() {
		return mReachability;
	}
	
	public ArrayList<Router> getRouters() {
		return mRouters;
	}
	public Rule getRule() {
		return mRule;
	}
	public int getBegin() {
		return mBegin;
	}
	public int getInterval() {
		return mInterval;
	}
	public int getSteps() {
		return mSteps;
	}
	public int getNumber() {
		return mNumber;
	}
	
}
