package simulation;

public class PacketTransmission extends Event {
	
	private Node destinationNode;
	private Rule rule;
	private int size;
	
	public PacketTransmission(Node destination, Rule rule, int size, int time) {
		super(EventType.PACKETTRANSMISSION, time);
		this.destinationNode = destination;
		this.rule = rule;
		this.size = size;
	}
	
	public PacketTransmission(Node destination, Rule rule, int time) {
		super(EventType.PACKETTRANSMISSION, time);
		this.destinationNode = destination;
		this.rule = rule;
		this.size = 1;
	}

	public Node getDestinationNode() {
		return destinationNode;
	}

	public Rule getRule() {
		return rule;
	}

	public int getPacketSize() {
		return size;
	}

}
