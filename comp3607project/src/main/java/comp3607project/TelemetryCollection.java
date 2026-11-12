package comp3607project;

import java.util.ArrayList;

public class TelemetryCollection {
    private ArrayList<TelemetryEvent> events;

    public TelemetryCollection() {
        events = new ArrayList<>();
    }

    public void addEvent(TelemetryEvent event) {
        events.add(event);
    }

    public ArrayList<TelemetryEvent> getEvents() {
        ArrayList<TelemetryEvent> eventsCopy = new ArrayList<>(events);
        return eventsCopy;
    }

    public int getEventCount() {
        return events.size();
    }

    public void clearEvents() {
        events.clear();
    }
}
