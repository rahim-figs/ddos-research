package simulation;
import java.util.ArrayList;


public class RouterSet {

	private ArrayList<Router> routerSet;
	private String name;
	
	public RouterSet(String name) {
		this.name = name;
		this.routerSet = new ArrayList<Router>();
	}
	
	public RouterSet(String name, ArrayList<Router> routerSet) {
		this.name = name;
		this.routerSet = routerSet;
	}

	public ArrayList<Router> getRouterSet() {
		return routerSet;
	}

	public void addRouter(Router router) {
		routerSet.add(router);
	}
	
	public String getName() {
		return this.name;
	}
	
}
