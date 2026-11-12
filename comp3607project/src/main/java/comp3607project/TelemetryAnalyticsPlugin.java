package comp3607project;

import bluej.extensions2.*;
import bluej.extensions2.event.*;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

public class TelemetryAnalyticsPlugin extends Extension implements PackageListener, CompileListener, InvocationFinishedListener, ClassListener
{
    private TelemetryCollection eventsCollection = new TelemetryCollection();
    private EditorTracker editorTracker = new EditorTracker(eventsCollection);
    private TelemetrySession session = new TelemetrySession();
    private int openPackages = 0;
    private HashSet<String> knownClasses = new HashSet<>();

    public void startup(BlueJ bluej) {
        bluej.addPackageListener(this);
        bluej.addCompileListener(this);
        bluej.addInvocationFinishedListener(this);
        bluej.addClassListener(this);
    }

    public void packageOpened(PackageEvent pe) {
        try {
            String projectName = pe.getPackage().getProject().getName();

            if (openPackages == 0) {
                eventsCollection.clearEvents();
                knownClasses.clear();
                session.startSession();
            }
            openPackages++;

            TelemetryEvent event = new TelemetryEvent("Package_Opened", projectName);
            eventsCollection.addEvent(event);

            System.out.println("Project opened: " + projectName);
            System.out.println("Time logged: " + event.getTime());

            editorTracker.startTracking(pe.getPackage());
        }catch (ExtensionException e){
            System.out.println("Error: Unable to retrieve project information.");
        }
    }

    public void packageClosing(PackageEvent pe) {

        try{
            String projectName = pe.getPackage().getProject().getName();

            TelemetryEvent event = new TelemetryEvent("Package_Closed", projectName);
            eventsCollection.addEvent(event);

            System.out.println("Project closed: " + projectName);
            System.out.println("Time logged: " + event.getTime());

        }catch (ExtensionException e){
            System.out.println("Error: Unable to retrieve project information.");
        }finally{
            if (openPackages > 0) {
                openPackages--;
            }

            if (openPackages == 0) {
                session.endSession();
                editorTracker.stopTracking();
                exportReports();
            }
        }
    }

    public boolean isCompatible() {
        return (getExtensionsAPIVersionMajor() >= 3);
    }

    public String getVersion() {
        return "1.0";
    }

    public String getName() {
        return "Telemetry Analytics Plugin";
    }

    public void terminate() {
        if (session.getStarted() != null && session.getEnded() == null) {
            session.endSession();
            editorTracker.stopTracking();
            exportReports();
        }

        System.out.println("Session lasted: " + session.getSessionDuration() + " seconds.");
    }

    public String getDescription() {
        return "This plugin is used for collecting, formatting and analysing telemetry data produced using the BlueJ coding editor.";
    }

    public String getProjectNameCompilation(CompileEvent ce) {
        File[] files = ce.getFiles();

        if (files != null && files.length > 0) {
            File parentFile = files[0].getParentFile();

            if (parentFile != null) {
                return parentFile.getName();
            }
        }

        return "Unknown_Project";
    }

    public void compileStarted(CompileEvent ce) {
        String projectName = getProjectNameCompilation(ce);

        TelemetryEvent event = new TelemetryEvent("Compilation_Started", projectName);
        eventsCollection.addEvent(event);

        System.out.println("Compilation started for: " + projectName);
        System.out.println("Compilation started at: " + event.getTime());
    }

    public void compileError(CompileEvent ce) {
        String projectName = getProjectNameCompilation(ce);

        TelemetryEvent event = new TelemetryEvent("Compilation_Error", projectName);

        event.setErrorMessage(ce.getErrorMessage());
        event.setErrorLineNumber(ce.getErrorLineNumber());

        File[] files = ce.getFiles();

        if (files != null && files.length > 0 && files[0] != null) {
            event.setErrorFileName(files[0].getName());
        }
        eventsCollection.addEvent(event);

        System.out.println("Compilation error for: " + projectName);
        System.out.println("Error message: " + event.getErrorMessage());
        System.out.println("Error line: " + event.getErrorLineNumber());
        System.out.println("Error file: " + event.getErrorFileName());
        System.out.println("Compilation error at: " + event.getTime());
    }

    public void compileWarning(CompileEvent ce) {
        String projectName = getProjectNameCompilation(ce);

        TelemetryEvent event = new TelemetryEvent("Compilation_Warning", projectName);
        eventsCollection.addEvent(event);

        System.out.println("Compilation warning for: " + projectName);
        System.out.println("Compilation warning at: " + event.getTime());
    }

    public void compileSucceeded(CompileEvent ce) {
        String projectName = getProjectNameCompilation(ce);

        TelemetryEvent event = new TelemetryEvent("Compilation_Succeeded", projectName);
        eventsCollection.addEvent(event);

        System.out.println("Compilation succeeded for: " + projectName);
        System.out.println("Compilation succeeded at: " + event.getTime());
    }

    public void compileFailed(CompileEvent ce) {
        String projectName = getProjectNameCompilation(ce);

        TelemetryEvent event = new TelemetryEvent("Compilation_Failed", projectName);
        eventsCollection.addEvent(event);

        System.out.println("Compilation failed for: " + projectName);
        System.out.println("Compilation failed at: " + event.getTime());
    }

    public void invocationFinished(InvocationFinishedEvent ife) {
        try {
            String projectName = ife.getPackage().getProject().getName();

            TelemetryEvent event;

            if (ife.getEventType() == InvocationFinishedEvent.EventType.EXCEPTION_EXIT) {

                event = new TelemetryEvent("Runtime_exception", projectName);
            }

            else{
                event = new TelemetryEvent("Invocation_Finished", projectName);
            }
            event.setClassName(ife.getClassName());
            event.setMethodName(ife.getMethodName());
            eventsCollection.addEvent(event);

            System.out.println("Invocation finished for: " + projectName);
            System.out.println("Invocation finished at: " + event.getTime());

        }catch (ExtensionException e){
            System.out.println("Error: Unable to retrieve project information.");
        }
    }

    public void classStateChanged(ClassEvent ce) {
        try {
            String projectName = ce.getPackage().getProject().getName();
            String className = ce.getBClass().getName();
            
            if (!knownClasses.contains(className)) { 
                TelemetryEvent createdEvent = new TelemetryEvent("Class_Created", projectName);
                createdEvent.setClassName(className);

                eventsCollection.addEvent(createdEvent);
                knownClasses.add(className);

                System.out.println("Class created: " + className);
            }
            TelemetryEvent event = new TelemetryEvent("Class_State_Changed", projectName);
            event.setClassName(className);
            eventsCollection.addEvent(event);

            System.out.println("Class state changed for: " + projectName);
            System.out.println("Class state changed at: " + event.getTime());
        }catch (ExtensionException e){
            System.out.println("Error: Unable to retrieve project information.");
        }
    }

    public void classNameChanged(ClassEvent ce) {
        try {
            String projectName = ce.getPackage().getProject().getName();
            String className = ce.getBClass().getName();

            TelemetryEvent event = new TelemetryEvent("Class_Name_Changed", projectName);
            event.setClassName(className);
            eventsCollection.addEvent(event);

            System.out.println("Class name changed for: " + projectName);
            System.out.println("Class name changed at: " + event.getTime());
        }catch (ExtensionException e){
            System.out.println("Error: Unable to retrieve project information.");
        }
    }

    public void classRemoved(ClassEvent ce) {
        try {
            String projectName = ce.getPackage().getProject().getName();
            String className = ce.getBClass().getName();
            
            knownClasses.remove(className);

            TelemetryEvent event = new TelemetryEvent("Class_Removed", projectName);
            event.setClassName(className);
            eventsCollection.addEvent(event);

            System.out.println("Class removed for: " + projectName);
            System.out.println("Class removed at: " + event.getTime());
        }catch (ExtensionException e){
            System.out.println("Error: Unable to retrieve project information.");
        }
    }

    private void exportReports() {
        try {
            DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            LocalDateTime startTime = session.getStarted();

            if (startTime == null) {
                return;
            }

            String time = startTime.format(dtFormatter);

            Path jsonPath = Path.of(System.getProperty("user.home"), "telemetry-report-" + time + ".json");
            Path pdfPath = Path.of(System.getProperty("user.home"), "telemetry-report-" + time + ".pdf");

            new JSONExporter().export(eventsCollection, session, jsonPath);
            new PDFReportGenerator().generateReport (eventsCollection, session, pdfPath);

            System.out.println("Telemetry reports exported successfully.");
        } 
        catch (Exception e){
            System.err.println("Failed to export telemetry reports: "+ e.getMessage());
        }
    }
}