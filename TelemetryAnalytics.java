package comp3607project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class TelemetryAnalytics {
    public int getTotalCompilations(ArrayList<TelemetryEvent> events) {
        int compilationCount = 0;

        if (events== null) {
            return 0;
        }

        for(TelemetryEvent event: events) {
            if (event!= null && "Compilation_Started".equals(event.getEventType())) {
                compilationCount++;
            }
        }

        return compilationCount;
    }

    public int getSuccessfulCompilationsCount(ArrayList<TelemetryEvent> events) {
        int successfulCompilations = 0;

        if (events== null) {
            return 0;
        }

        for(TelemetryEvent event: events) {
            if (event!=null && "Compilation_Succeeded".equals(event.getEventType())) {
                successfulCompilations++;
            }
        }

        return successfulCompilations;
    }

    public int getFailedCompilationsCount(ArrayList<TelemetryEvent> events) {
        int failedCompilations = 0;

        if (events==null) {
            return 0;
        }

        for(TelemetryEvent event: events) {
            if (event!=null && "Compilation_Failed".equals(event.getEventType())) {
                failedCompilations++;
            }
        }

        return failedCompilations;
    }

    public int getErrorCount(ArrayList<TelemetryEvent> events) {
        int errorCount = 0;

        if (events == null) {
            return 0;
        }

        for(TelemetryEvent event: events) {
            if (event != null && "Compilation_Error".equals(event.getEventType())) {
                errorCount++;
            }
        }

        return errorCount;
    }

    public int getExecutionCount(ArrayList<TelemetryEvent> events) {
        int executionCount = 0;

        if (events == null) {
            return 0;
        }

        for(TelemetryEvent event: events) {
            if (event != null && ("Invocation_Finished".equals(event.getEventType()) || "Runtime_exception".equals(event.getEventType()))){
                executionCount++;
            }
        }

        return executionCount;
    }
        
    public double getAverageTimeBetweenCompilations(ArrayList<TelemetryEvent> events) {
        ArrayList<TelemetryEvent> compilationEvents = new ArrayList<>();
        double secondsTotal= 0;
        int intervalCount= 0;

        if (events == null) {
            return 0;
        }

        for (TelemetryEvent event: events) {
            if (event!=null && "Compilation_Started".equals(event.getEventType()) && event.getTime()!=null) {
                compilationEvents.add(event);
            }
        }

        if (compilationEvents.size() <2) {
            return 0;
        }

        Collections.sort(compilationEvents, Comparator.comparing(TelemetryEvent::getTime));

        for (int i = 1; i < compilationEvents.size(); i++) {
            LocalDateTime previousTime = compilationEvents.get(i - 1).getTime();
            LocalDateTime currentTime = compilationEvents.get(i).getTime();
            double seconds = Duration.between(previousTime, currentTime).toMillis() /1000.0;
            secondsTotal += seconds;
            intervalCount++;
        }

        return secondsTotal/intervalCount;
    }

    public long getTotalActiveTime(ArrayList<TelemetryEvent> events) {
        long totalActiveTime = 0;

        if (events == null) {
            return totalActiveTime;
        }

        for (TelemetryEvent event : events) {

            if (event != null) {
                
                totalActiveTime += event.getActiveTime();
            }
        }

        return totalActiveTime/1000;
    }

    public long getTotalIdleTime(ArrayList<TelemetryEvent> events) {
        long totalIdleTime = 0;

        if (events == null) {
            return totalIdleTime;
        }

        for (TelemetryEvent event : events) {

            if (event != null) {

                totalIdleTime +=event.getIdleTime();
            }
        }

        return totalIdleTime/1000;
    }

    public HashMap<String, Integer> getErrorFrequency(ArrayList<TelemetryEvent> events) {
        HashMap<String, Integer> errorFrequency = new HashMap<>();

        if (events == null) {
            return errorFrequency;
        }

        for (TelemetryEvent event: events) {
            if (event != null && "Compilation_Error".equals(event.getEventType())) {

                String errorMessage = event.getErrorMessage();

                if (errorMessage == null || errorMessage.isEmpty()) {
                    errorMessage = "Unknown error";
                }

                if (errorFrequency.containsKey(errorMessage)) {
                    int count = errorFrequency.get(errorMessage);
                    errorFrequency.put(errorMessage, count + 1);
                } else {
                    errorFrequency.put(errorMessage, 1);
                }
            }
        }

        return errorFrequency;
    }
}
