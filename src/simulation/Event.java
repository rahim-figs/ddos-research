package simulation;

public class Event {

	public enum EventType {
		PACKETTRANSMISSION, SCORETRANSMISSION, SELECTIONWINDOWENDED
	}
	
	private int time;
	private EventType type;
	
	public Event(EventType type, int time) {
		this.type = type;
		this.time = time;
	}
	
	public int getTime() {
		return time;
	}

	public EventType getType() {
		return type;
	}

}
