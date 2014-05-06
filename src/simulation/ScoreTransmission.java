package simulation;
import java.util.HashMap;


public class ScoreTransmission extends Event {
	
	private Node destination;
	private HashMap<Rule, Float> scoreSet;
	
	public ScoreTransmission(Node destination, HashMap<Rule, Float> scoreSet, int time) {
		super(EventType.SCORETRANSMISSION, time);
		this.destination = destination;
		this.scoreSet = scoreSet;
	}

	public Node getDestination() {
		return destination;
	}

	public HashMap<Rule, Float> getScoreSet() {
		return scoreSet;
	}

}
