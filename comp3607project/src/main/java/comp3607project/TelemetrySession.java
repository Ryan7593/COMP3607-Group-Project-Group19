package comp3607project;

import java.time.Duration;
import java.time.LocalDateTime;

public class TelemetrySession {
    private LocalDateTime started;
    private LocalDateTime ended;

    public TelemetrySession() {
        this.started = null;
        this.ended = null;
    }

    public void startSession() {
        if (started==null || ended!=null) {
            started = LocalDateTime.now();
            ended = null;
        }
    }

    public void endSession() {
        if (started!=null && ended==null) {
            ended = LocalDateTime.now();
        }
    }

    public long getSessionDuration() {
        LocalDateTime endTime;
        
        if (started == null) {
            return 0;
        }

        if (ended == null) {
            endTime = LocalDateTime.now();
        }else {
            endTime = ended;
        }

        return Duration.between(started, endTime).getSeconds();
    }

    public LocalDateTime getStarted() {
        return this.started;
    }

    public LocalDateTime getEnded() {
        return this.ended;
    }
}
