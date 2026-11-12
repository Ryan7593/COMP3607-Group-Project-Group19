package comp3607project;

import bluej.extensions2.editor.DocumentListener;

public class EditorActivityListener implements DocumentListener {

    private final TelemetryCollection eventsCollection;
    private final String projectName;

    private long lastEditTime;
    private long activeTime;
    private long idleTime;

    private static final long IDLE_LIMIT = 30000;

    public EditorActivityListener(TelemetryCollection eventsCollection, String projectName) {
        this.eventsCollection = eventsCollection;
        this.projectName = projectName;

        lastEditTime = 0;
        activeTime = 0;
        idleTime = 0;
    }

    public void textReplaced(int offset, String oldText, String newText, int oldLineCount, int newLineCount) {
        long currentTime = System.currentTimeMillis();
        long activeInterval = 0;
        long idleInterval = 0;

        if (lastEditTime != 0) {
            long timeDifference = currentTime - lastEditTime;

            if (timeDifference <= IDLE_LIMIT) {
                activeInterval = timeDifference;
            } else {
                activeInterval = IDLE_LIMIT;
                idleInterval = timeDifference-IDLE_LIMIT;
            }
            activeTime += activeInterval;
            idleTime += idleInterval;
        }

        lastEditTime = currentTime;

        TelemetryEvent event = new TelemetryEvent("Text_Changed_Editor", projectName);

        event.setActiveTime(activeInterval);
        event.setIdleTime(idleInterval);

        eventsCollection.addEvent(event);

        System.out.println("Text changed in editor for: " + projectName);
        System.out.println("Active time: " + getActiveTime() + " seconds");
        System.out.println("Idle time: " + getIdleTime() + " seconds");
    }

    public long getActiveTime() {
        return activeTime / 1000;
    }

    public long getIdleTime() {
        return idleTime / 1000;
    }
}
