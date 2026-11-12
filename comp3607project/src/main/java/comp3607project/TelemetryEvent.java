package comp3607project;

import java.time.LocalDateTime;

public class TelemetryEvent {
    private final String eventType;
    private final String projectName;
    private final LocalDateTime time;
    private String errorMessage;
    private int errorLineNumber;
    private String errorFileName;
    private long activeTime;
    private long idleTime;
    private String className;
    private String methodName;

    public TelemetryEvent(String eventType, String projectName) {
        this.eventType = eventType;
        this.projectName = projectName;
        this.time = LocalDateTime.now();
    }

    public String getEventType() {
        return this.eventType;
    }
    
    public String getProjectName() {
        return this.projectName;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public String getErrorMessage() {
    return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorLineNumber() {
        return errorLineNumber;
    }

    public void setErrorLineNumber(int errorLineNumber) {
        this.errorLineNumber = errorLineNumber;
    }

    public String getErrorFileName() {
        return errorFileName;
    }

    public void setErrorFileName(String errorFileName) {
        this.errorFileName = errorFileName;
    }

    
    public long getActiveTime() {
       return activeTime;
    }

    public void setActiveTime(long activeTime) {
       this.activeTime = activeTime;
    }

    public long getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(long idleTime) {
        this.idleTime = idleTime;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName (String methoName) {
        this.methodName = methoName;
    }
}
