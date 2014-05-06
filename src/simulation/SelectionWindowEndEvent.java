package simulation;

public class SelectionWindowEndEvent extends Event {
	
	private Node node;
	
	public SelectionWindowEndEvent(Node node, int time) {
		super(EventType.SELECTIONWINDOWENDED, time);
		this.node = node;
	}

	public Node getNode() {
		return node;
	}

}
