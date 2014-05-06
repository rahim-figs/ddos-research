package simulation;
import java.util.ArrayList;
import java.util.Random;

import javax.persistence.EntityManager;

import org.ddosm.EMF;


public class Simulation {


	private Topology mTopology;
	private EventManager mEventManager;
	private AnalysisManager mAnalysisManager;
	private StatsManager mStatsManager;
	
	private float gamma;
	private float alpha;
	private float a;
	private float b1;
	private float b2;
	private float b3;
	private float epsilon;
	private float omega;
	private float beta;
	private float tau;
	private float upsilon;
	
	public Simulation init() {
		mEventManager = new EventManager(this);
		mAnalysisManager = new AnalysisManager(this);
		mStatsManager = new StatsManager(this);
		
		mTopology = new Topology(this);
		
		return this;
	}
	
	public float getA() {
		return this.a;
	}
	
	public Simulation setA(float a) {
		this.a = a;
		return this;
	}
	
	public Topology getTopology() {
		return mTopology;
	}
	
	public EventManager getEventManager() {
		return mEventManager;
	}
	
	public AnalysisManager getAnalysisManager() {
		return mAnalysisManager;
	}
	
	public StatsManager getStatsManager() {
		return mStatsManager;
	}
	
	public Simulation setGamma(float gamma) {
		this.gamma = gamma;
		return this;
	}
	
	public Simulation setAlpha(float alpha) {
		this.alpha = alpha;
		return this;
	}
	
	public Simulation setB1(float b1) {
		this.b1 = b1;
		return this;
	}
	
	public Simulation setB2(float b2) {
		this.b2 = b2;
		return this;
	}
	
	public Simulation setB3(float b3) {
		this.b3 = b3;
		return this;
	}
	
	public Simulation setEpsilon(float epsilon) {
		this.epsilon = epsilon;
		return this;
	}
	
	public Simulation setOmega(float omega) {
		this.omega = omega;
		return this;
	}
	
	public Simulation setBeta(float beta) {
		this.beta = beta;
		return this;
	}
	
	public Simulation setTau(float tau) {
		this.tau = tau;
		return this;
	}
	
	public Simulation setUpsilon(float upsilon) {
		this.upsilon = upsilon;
		return this;
	}
	
	public float getGamma() {
		return gamma;
	}
	
	public float getAlpha() {
		return alpha;
	}
	
	public float getB1() {
		return b1;
	}
	
	public float getB2() {
		return b2;
	}
	
	public float getB3() {
		return b3;
	}
	
	public float getEpsilon() {
		return epsilon;
	}
	
	public float getOmega() {
		return omega;
	}
	
	public float getBeta() {
		return beta;
	}
	
	public float getTau() {
		return tau;
	}
	
	public float getUpsilon() {
		return upsilon;
	}
	
	public ArrayList<Attack> addRandomAttacks(int n) {
		
		Topology topology = getTopology();
		ArrayList<Host> hosts = topology.getHosts();
		int hostsCount = hosts.size();
		ArrayList<RouterSet> rings = topology.getRings();
		int ringsCount = rings.size();
		
		if (n > hostsCount) {
			n = hostsCount;
		}
		
		ArrayList<Attack> attacks = new ArrayList<Attack>(n);
		
		for (int i = n - 1; i >= 0; i--) {
			Host randomHost = hosts.remove((int) (Math.random() * i));
			Rule rule = randomHost.getRule();
			
			int randomRing = ringsCount - 1;
			int routerCount = rings.get(randomRing).getRouterSet().size();
			int numberOfPackets = 30 + new Random().nextInt(50);
			
			Attack attack = topology.createAttack(
					randomRing + 1, // ring 
					routerCount, // number of routers
					rule, // rule
					10, // begin
					5, // interval
					10, // steps
					numberOfPackets // number of packets
				);
			
			attack.computeReachability();
			
			attacks.add(attack);
			
			getStatsManager().addAttackStat(new AttackStat(randomRing + 1, randomHost, routerCount, numberOfPackets));
		}
		
		return attacks;
		
	}
	
	public static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}
	
}
