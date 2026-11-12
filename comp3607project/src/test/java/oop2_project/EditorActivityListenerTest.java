package comp3607project;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EditorActivityListenerTest {

    @Test
    public void testTextReplacedCreatesTelemetryEvent() {
        TelemetryCollection collection = new TelemetryCollection();
        EditorActivityListener listener = new EditorActivityListener(collection, "TestProject");

        listener.textReplaced(0, "", "a", 0, 0);

        assertEquals(1, collection.getEventCount());
        assertEquals("Text_Changed_Editor", collection.getEvents().get(0).getEventType());
        assertEquals("TestProject", collection.getEvents().get(0).getProjectName());
    }
}
