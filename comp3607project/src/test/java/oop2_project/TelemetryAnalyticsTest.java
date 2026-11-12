package comp3607project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class TelemetryAnalyticsTest {

    @Test
    public void testCompilationCounts() {
        ArrayList<TelemetryEvent> events = new ArrayList<>();
        events.add(new TelemetryEvent("Compilation_Started", "TestProject"));
        events.add(new TelemetryEvent("Compilation_Started", "TestProject"));
        events.add(new TelemetryEvent("Compilation_Succeeded", "TestProject"));
        events.add(new TelemetryEvent("Compilation_Failed", "TestProject"));
        events.add(new TelemetryEvent("Compilation_Error", "TestProject"));

        TelemetryAnalytics analytics = new TelemetryAnalytics();

        assertEquals(2, analytics.getTotalCompilations(events));
        assertEquals(1, analytics.getSuccessfulCompilationsCount(events));
        assertEquals(1, analytics.getFailedCompilationsCount(events));
        assertEquals(1, analytics.getErrorCount(events));
    }

    @Test
    public void testExecutionCountIncludesRuntimeExceptions() {
        ArrayList<TelemetryEvent> events = new ArrayList<>();
        events.add(new TelemetryEvent("Invocation_Finished", "TestProject"));
        events.add(new TelemetryEvent("Runtime_exception", "TestProject"));

        TelemetryAnalytics analytics = new TelemetryAnalytics();

        assertEquals(2, analytics.getExecutionCount(events));
    }

    @Test
    public void testActiveAndIdleTime() {
        ArrayList<TelemetryEvent> events = new ArrayList<>();

        TelemetryEvent firstEvent = new TelemetryEvent("Text_Changed_Editor", "TestProject");
        firstEvent.setActiveTime(1500);
        firstEvent.setIdleTime(1000);

        TelemetryEvent secondEvent = new TelemetryEvent("Text_Changed_Editor", "TestProject");
        secondEvent.setActiveTime(2500);
        secondEvent.setIdleTime(2000);

        events.add(firstEvent);
        events.add(secondEvent);

        TelemetryAnalytics analytics = new TelemetryAnalytics();

        assertEquals(4, analytics.getTotalActiveTime(events));
        assertEquals(3, analytics.getTotalIdleTime(events));
    }

    @Test
    public void testErrorFrequency() {
        ArrayList<TelemetryEvent> events = new ArrayList<>();

        TelemetryEvent firstError = new TelemetryEvent("Compilation_Error", "TestProject");
        firstError.setErrorMessage("Missing semicolon");

        TelemetryEvent secondError = new TelemetryEvent("Compilation_Error", "TestProject");
        secondError.setErrorMessage("Missing semicolon");

        TelemetryEvent thirdError = new TelemetryEvent("Compilation_Error", "TestProject");
        thirdError.setErrorMessage("Unknown variable");

        events.add(firstError);
        events.add(secondError);
        events.add(thirdError);

        TelemetryAnalytics analytics = new TelemetryAnalytics();
        HashMap<String, Integer> frequency = analytics.getErrorFrequency(events);

        assertEquals(2, frequency.get("Missing semicolon"));
        assertEquals(1, frequency.get("Unknown variable"));
    }

    @Test
    public void testAverageTimeWithTwoCompilationEvents() {
        ArrayList<TelemetryEvent> events = new ArrayList<>();
        events.add(new TelemetryEvent("Compilation_Started", "TestProject"));
        events.add(new TelemetryEvent("Compilation_Started", "TestProject"));

        TelemetryAnalytics analytics = new TelemetryAnalytics();
        double average = analytics.getAverageTimeBetweenCompilations(events);

        assertTrue(average >= 0);
    }
}
