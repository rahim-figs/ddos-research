package simulation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;


public class EventManager {

	private HashMap<Integer, LinkedList<Event>> mEventsData;
	private int mCurrentPlayingTime;
	private int mMaxTimeSoFar;
	private Simulation Simulation;
	private int mCurrentPlayingSubEventIndex;
	
	public EventManager(Simulation simulation) {
		Simulation = simulation;
		
		mEventsData = new HashMap<Integer, LinkedList<Event>>();
		
		mCurrentPlayingTime = 0;
		mCurrentPlayingSubEventIndex = 0;
		mMaxTimeSoFar = 0;
		
	}
	
	public void reset() {
		
		mEventsData.clear();
		mCurrentPlayingTime = 0;
		mCurrentPlayingSubEventIndex = 0;
	}
	
	public HashMap<Integer, LinkedList<Event>> getEvents() {
		return mEventsData;
	}
	
	public void addPacketTransmission(Rule rule, Node destinationNode) {
		int time = ++mMaxTimeSoFar;
		LinkedList<Event> eventsAtTime = mEventsData.get(time);
		if (eventsAtTime == null) {
			eventsAtTime = new LinkedList<Event>();
			mEventsData.put(time, eventsAtTime);
		}
		eventsAtTime.add(new PacketTransmission(destinationNode, rule, time));
	}
	
	public void addPacketTransmissionAtCurrentTime(Rule rule, Node destinationNode) {
		int time = mCurrentPlayingTime + 1;
		LinkedList<Event> eventsAtTime = mEventsData.get(time);
		if (eventsAtTime == null) {
			eventsAtTime = new LinkedList<Event>();
			mEventsData.put(time, eventsAtTime);
		}
		
		eventsAtTime.add(new PacketTransmission(destinationNode, rule, time));
	}
	
	public void addPacketTransmission(Rule rule, Node destinationNode, int time) {
		
		LinkedList<Event> eventsAtTime = mEventsData.get(time);
		if (eventsAtTime == null) {
			eventsAtTime = new LinkedList<Event>();
			mEventsData.put(time, eventsAtTime);
		}
		eventsAtTime.add(new PacketTransmission(destinationNode, rule, time));
		
	}
	
	public void addWindowEndedEvent(Node node) {
		int time = mCurrentPlayingTime;
		LinkedList<Event> eventsAtTime = mEventsData.get(time);
		if (eventsAtTime == null) {
			eventsAtTime = new LinkedList<Event>();
			mEventsData.put(time, eventsAtTime);
		}
		eventsAtTime.add(mCurrentPlayingSubEventIndex + 1, new SelectionWindowEndEvent(node, time));
	}
	
	private void playEvent(Event event) {

		if (Event.EventType.PACKETTRANSMISSION == event.getType()) {		
			
			PacketTransmission packetTransmission = (PacketTransmission) event;
			packetTransmission.getDestinationNode().receivePacket(packetTransmission.getRule(), packetTransmission.getPacketSize());
			
		} else if (Event.EventType.SCORETRANSMISSION == event.getType()) {
			
			ScoreTransmission scoreTransmission = (ScoreTransmission) event;
			scoreTransmission.getDestination().receiveScores(scoreTransmission.getScoreSet());
			
		} else if (Event.EventType.SELECTIONWINDOWENDED == event.getType()) {
			
			SelectionWindowEndEvent selectionWindowEvent = (SelectionWindowEndEvent) event;
			selectionWindowEvent.getNode().endWindow();
		}
		
	}
	
	public void play() {
		
		mCurrentPlayingTime = 0;
		
		int lastValidTime = 0;
		int i = 0;
		
		while (true) {
			int time = i++;
			LinkedList<Event> events = mEventsData.get(time);
			if (events != null && events.size() > 0) {
				mCurrentPlayingTime = time;
				int numEvents = events.size();
				for (int j = 0; j < numEvents; j++) {
					mCurrentPlayingSubEventIndex = j;
					playEvent(events.get(j));
					numEvents = events.size();
				}
				lastValidTime = time;
				
			} else if (time - lastValidTime > 10) {
				break;
			}
			
//			if (time - lastAttacksCheckedAtHosts > 5) {
//				Simulation.getTopology().checkAttacksAtAllHosts();
//				lastAttacksCheckedAtHosts = time;
//			}
			
			
		}
		
	}
	
	public void attackDetected(Router router, Rule rule, float confidence) {
//		System.out.println("#### Attack detected!!!");
//		System.out.println("Detected at " + router.getName() + ", rule = " + rule.getRuleString() + ", Confidence = " + confidence);
		Simulation.getAnalysisManager().addDetection(new Detection(mCurrentPlayingTime, router, rule, confidence));
	}
	
	public void addScoreTransmission(HashMap<Rule, Float> scoreMap, Node destinationNode) {
		
		int time = mCurrentPlayingTime;
		LinkedList<Event> eventsAtTime = mEventsData.get(time);
		if (eventsAtTime == null) {
			eventsAtTime = new LinkedList<Event>();
			mEventsData.put(time, eventsAtTime);
		}
		
		eventsAtTime.add(mCurrentPlayingSubEventIndex + 1, new ScoreTransmission(destinationNode, scoreMap, time));
		
	}
	
	
	public void displayAllEvents() {
		
		ArrayList<Integer> times = new ArrayList<Integer>(mEventsData.keySet());
		Collections.sort(times);
		
		System.out.println("Events:");
		
		int count = 0;
		for (int i = 0; i < times.size(); i++) {
			int time = times.get(i);
			LinkedList<Event> events = mEventsData.get(time);
			if (events != null && events.size() > 0) {
				for (Event event : events) {

					System.out.println("Event " + (i + 1) + ", time =  " + event.getTime());
					
					if (Event.EventType.PACKETTRANSMISSION == event.getType()) {
						
						PacketTransmission packetTransmission = (PacketTransmission) event;
						System.out.println("  Packet transmission to " + packetTransmission.getDestinationNode().getName() + ", rule: " + packetTransmission.getRule().getRuleString());
						
					} else if (Event.EventType.SCORETRANSMISSION == event.getType()) {
						
						ScoreTransmission scoreTransmission = (ScoreTransmission) event;
						System.out.println("  Score transmission to " + scoreTransmission.getDestination().getName());
						
					} else if (Event.EventType.SELECTIONWINDOWENDED == event.getType()) {
						
						SelectionWindowEndEvent selectionWindowEvent = (SelectionWindowEndEvent) event;
						System.out.println("  Selection window ended for " + selectionWindowEvent.getNode().getName());
						
					}
					
					System.out.println();
					
					count++;
				}
			}
		}
		
		System.out.println("Displayed " + count + " events.");
		
	}
}
