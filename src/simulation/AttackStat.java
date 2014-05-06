package simulation;

public class AttackStat {

	public int ring;
	public Host host;
	public int routersInvolved;
	public int packets;
	
	public AttackStat(int ring, Host host, int routersInvolved, int packets) {
		this.ring = ring;
		this.host = host;
		this.routersInvolved = routersInvolved;
		this.packets = packets;
	}
}
