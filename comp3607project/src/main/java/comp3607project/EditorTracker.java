package comp3607project;

import bluej.extensions2.BClass;
import bluej.extensions2.BPackage;
import bluej.extensions2.Extension;
import bluej.extensions2.ExtensionException;
import bluej.extensions2.editor.JavaEditor;

import java.util.HashMap;
import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class EditorTracker {
    private TelemetryCollection eventsCollection;
    private HashMap<String, JavaEditor> trackedEditors = new HashMap<>();
    private HashMap<String, EditorActivityListener> trackedListeners = new HashMap<>();
    private Timeline checkTimer;
    private HashMap<String, BPackage> trackedPackages = new HashMap<>();

    public EditorTracker(TelemetryCollection eventsCollection) {
        this.eventsCollection = eventsCollection;
    }

    public void trackEditors(BPackage pkg) {
        try {
            String projectName = pkg.getProject().getName();

            for (BClass bClass: pkg.getClasses()) {
                String className = bClass.getName();
                String key = projectName + ": " + pkg.getName() + ": " + className;

                JavaEditor editor = bClass.getJavaEditorIfOpen();

                if (editor != null) {
                    if (!trackedEditors.containsKey(key) || trackedEditors.get(key) != editor) {

                        if (trackedEditors.containsKey(key)) {
                            removeTrackedEditor(key);
                        }

                        EditorActivityListener listener = new EditorActivityListener(eventsCollection, projectName);
                        editor.addDocumentListener(listener);

                        trackedEditors.put(key, editor);
                        trackedListeners.put(key, listener);

                        System.out.println("Tracking editor for: " + className);
                    }
                }else if (trackedEditors.containsKey(key)) {
                    removeTrackedEditor(key);
                }
            }
        }catch (ExtensionException e) {
            System.out.println("Error tracking editors: " + e.getMessage());
        }
    }

    public void startTracking(BPackage pkg) {
        try {
            String projectName = pkg.getProject().getName();
            String packageKey = projectName + ": " + pkg.getName();

            trackedPackages.put(packageKey, pkg);
            trackEditors(pkg);

            if (checkTimer == null) {
                checkTimer = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
                    for (BPackage trackedPackage: trackedPackages.values()) {
                        trackEditors(trackedPackage);
                    }
                }));

                checkTimer.setCycleCount(Timeline.INDEFINITE);
                checkTimer.play();
            }
        } catch (ExtensionException e) {
            System.out.println("Error starting editor tracking: " + e.getMessage());
        }
    }

    private void removeTrackedEditor(String key) {
        JavaEditor editor = trackedEditors.get(key);
        EditorActivityListener listener = trackedListeners.get(key);

        if (editor != null && listener != null) {
            editor.removeDocumentListener(listener);
        }

        trackedEditors.remove(key);
        trackedListeners.remove(key);
    }

    public void stopTracking() {
        if (checkTimer != null) {
            checkTimer.stop();
            checkTimer = null;
        }

        trackedPackages.clear();

        ArrayList<String> editorKeys = new ArrayList<>(trackedEditors.keySet());

        for (String key : editorKeys) {
            removeTrackedEditor(key);
        }

        trackedEditors.clear();
        trackedListeners.clear();
    }
}
