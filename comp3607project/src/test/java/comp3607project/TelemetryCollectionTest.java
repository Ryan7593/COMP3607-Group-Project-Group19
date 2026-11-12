package comp3607project;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TelemetryCollectionTest {

    @Test
    public void testAddEvent() {
        TelemetryCollection collection = new TelemetryCollection();
        TelemetryEvent event = new TelemetryEvent("Compilation_Started", "TestProject");

        collection.addEvent(event);

        assertEquals(1, collection.getEventCount());
        assertEquals("Compilation_Started", collection.getEvents().get(0).getEventType());
    }

    @Test
    public void testClearEvents() {
        TelemetryCollection collection = new TelemetryCollection();
        collection.addEvent(new TelemetryEvent("Compilation_Started", "TestProject"));

        collection.clearEvents();

        assertEquals(0, collection.getEventCount());
    }

    @Test
    public void testGetEventsReturnsCopy() {
        TelemetryCollection collection = new TelemetryCollection();
        collection.addEvent(new TelemetryEvent("Compilation_Started", "TestProject"));

        ArrayList<TelemetryEvent> events = collection.getEvents();
        events.clear();

        assertEquals(1, collection.getEventCount());
    }
}
