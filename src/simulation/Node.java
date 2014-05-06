package simulation;
import java.util.ArrayList;
import java.util.HashMap;

public class Node {

	public enum NodeType {
		HOST, ROUTER
	}
	
	private NodeType nodeType;
	private String name;
	protected HashMap<Rule, Integer> mFrequencySet;
	protected int mPacketsCount;
	protected int mWindowsElapsed;
	protected int mWindowLength;
	protected Topology Topology;
	
	public Node(NodeType nodeType, int windowLength, Topology topology) {
		this.nodeType = nodeType;
		mFrequencySet = new HashMap<Rule, Integer>();
		mPacketsCount = 0;
		mWindowsElapsed = 0;
		mWindowLength = windowLength;
		Topology = topology;
	}

	public String getName() {
		return name;
	}
	
	public int getWindowLength() {
		return mWindowLength;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NodeType getNodeType() {
		return nodeType;
	}
	
	public ArrayList<Node> getSuccessors() {
		return new ArrayList<Node>();
	}
	
	public int getTimeRoute(Node node) {
		return 0;
	}
	
	public void receivePacket(Rule rule, int size) {
		mPacketsCount++;
		
		if (mPacketsCount >= mWindowLength) {
			Topology.getSimulation().getEventManager().addWindowEndedEvent(this);
		}
	}
	
	public void receiveScores(HashMap<Rule, Float> scoresMap) {	}
	
	public void endWindow() {
		if (mPacketsCount >= mWindowLength) {
			mWindowsElapsed++;
			mPacketsCount = 0;
		}
	}

}
