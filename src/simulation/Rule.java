package simulation;

public class Rule {
	
	private String ruleString;
	private int hostId;
	
	public Rule(int id) {
		this.hostId = id;
		this.ruleString = "R" + id;
	}
	
	public int getId() {
		return hostId;
	}

	public String getRuleString() {
		return ruleString;
	}

}
