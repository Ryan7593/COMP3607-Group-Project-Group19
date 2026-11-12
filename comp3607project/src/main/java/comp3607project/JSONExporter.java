package comp3607project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JSONExporter {
    private final ObjectMapper objectMapper;

    public JSONExporter() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void export(TelemetryCollection collection, TelemetrySession session, Path outputPath) throws IOException {
        if (collection == null || session == null || outputPath == null) {
            throw new IllegalArgumentException("Collection, session and output path cannot be null.");
        }

        List<TelemetryEvent> events = collection.getEvents();
        TelemetryAnalytics analytics = new TelemetryAnalytics();

        Map<String, Object> report = new LinkedHashMap<>();

        Map<String, Object> sessionMetadata = new LinkedHashMap<>();

        LocalDateTime started = session.getStarted();
        LocalDateTime ended = session.getEnded();

        String startTime = null;
        String endTime = null;

        if (started != null) {
            startTime = started.toString();
        }

        if (ended != null) {
            endTime = ended.toString();
        }

        sessionMetadata.put("session_started", startTime);
        sessionMetadata.put("session_ended", endTime);
        sessionMetadata.put("session_duration_seconds", session.getSessionDuration());

        report.put("session_metadata", sessionMetadata);

        Map<String, Object> metrics = new LinkedHashMap<>();
        ArrayList<TelemetryEvent> analyticsEvents = new ArrayList<>(events);

        metrics.put("total_compilations", analytics.getTotalCompilations(analyticsEvents));

        metrics.put("successful_compilations", analytics.getSuccessfulCompilationsCount(analyticsEvents));

        metrics.put("failed_compilations", analytics.getFailedCompilationsCount(analyticsEvents));

        metrics.put("total_compilation_errors", analytics.getErrorCount(analyticsEvents));

        metrics.put("total_executions", analytics.getExecutionCount(analyticsEvents));

        metrics.put("average_time_between_compilations_seconds", analytics.getAverageTimeBetweenCompilations(analyticsEvents));

        metrics.put("total_active_time_seconds", analytics.getTotalActiveTime(analyticsEvents));

        metrics.put("total_idle_time_seconds", analytics.getTotalIdleTime(analyticsEvents));

        report.put("analytics", metrics);

        List<Map<String, Object>> eventData = new ArrayList<>();

        for (TelemetryEvent event: events) {

            if (event != null) {
                Map<String, Object> eventEntry = new LinkedHashMap<>();

                eventEntry.put("event_type", event.getEventType());
                eventEntry.put("project_name", event.getProjectName());

                String timestamp = null;

                if (event.getTime() != null) {
                    timestamp = event.getTime().toString();
                }

                eventEntry.put("timestamp", timestamp);
                eventEntry.put("active_time_ms", event.getActiveTime());
                eventEntry.put("idle_time_ms", event.getIdleTime());
                eventEntry.put("class_name", event.getClassName());
                eventEntry.put("method_name", event.getMethodName());

                if ("Compilation_Error".equals(event.getEventType())) {

                    eventEntry.put("error_message", event.getErrorMessage());
                    eventEntry.put("error_line_number", event.getErrorLineNumber());
                    eventEntry.put("error_file_name", event.getErrorFileName());
                }

                eventData.add(eventEntry);
            }
        }

        report.put("events", eventData);

        objectMapper.writeValue(outputPath.toFile(), report);
    }
}
