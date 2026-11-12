package comp3607project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TelemetrySessionTest {

    @Test
    public void testNewSessionHasNotStarted() {
        TelemetrySession session = new TelemetrySession();

        assertNull(session.getStarted());
        assertNull(session.getEnded());
        assertEquals(0, session.getSessionDuration());
    }

    @Test
    public void testStartSession() {
        TelemetrySession session = new TelemetrySession();

        session.startSession();

        assertNotNull(session.getStarted());
        assertNull(session.getEnded());
    }

    @Test
    public void testEndSession() {
        TelemetrySession session = new TelemetrySession();

        session.startSession();
        session.endSession();

        assertNotNull(session.getEnded());
        assertTrue(session.getSessionDuration() >= 0);
    }
}
