package simulation;

public class Host extends Node {
	
	private int capacity;
	private int id;
	private String name;
	private Rule mRule;
	
	public Host(int id, int capacity, int windowLength, Topology topology) {
		super(NodeType.HOST, windowLength, topology);
		this.id = id;
		this.capacity = capacity;
		
		this.name = "H" + this.id;
	}

	public int getCapacity() {
		return capacity;
	}
	
	public int id() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setRule(Rule rule) {
		mRule = rule;
	}
	
	public Rule getRule() {
		return mRule;
	}
	
	@Override
	public void receivePacket(Rule rule, int size) {
		mPacketsCount++;
//		System.out.println("Received packed at host " + getName() + " count = " + mPacketsCount);
	}
	
	public boolean checkAttack() {
		
		boolean attacked = false;
		if (mPacketsCount > capacity) {
//			System.out.println("----> Failed to mitigate attack: Host " + getName() + " could not handle traffic: " + mPacketsCount + " / " + capacity);
			attacked = true;
		} else {
//			System.out.println("No attack detected at Host " + getName() + " Stats " + mPacketsCount + " / " + capacity);
		}
		
		mPacketsCount = 0;
		
		return attacked;
	}

}
