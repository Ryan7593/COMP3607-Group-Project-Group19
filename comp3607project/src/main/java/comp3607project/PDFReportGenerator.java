package comp3607project;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PDFReportGenerator {

    private static final float LEFT = 50;
    private static final float TOP = 750;
    private static final float BOTTOM = 60;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void generateReport(TelemetryCollection collection, TelemetrySession session, Path outputPath) throws IOException {
        if (collection == null || session == null || outputPath == null) {
            throw new IllegalArgumentException("Collection, session and output path cannot be null.");
        }

        ArrayList<TelemetryEvent> events = collection.getEvents();

        TelemetryAnalytics analytics = new TelemetryAnalytics();

        int total = analytics.getTotalCompilations(events);
        int successful = analytics.getSuccessfulCompilationsCount(events);
        int failed = analytics.getFailedCompilationsCount(events);
        int errors = analytics.getErrorCount(events);
        int executions = analytics.getExecutionCount(events);
        double average = analytics.getAverageTimeBetweenCompilations(events);
        long activeTime = analytics.getTotalActiveTime(events);
        long idleTime = analytics.getTotalIdleTime(events);

        Map<String, Integer> errorFrequency = analytics.getErrorFrequency(events);

        try (PDDocument document = new PDDocument()) {
            addSummaryPage(document, session, total, successful, failed, errors, executions, average, activeTime, idleTime);
            addTimelinePages(document, events);
            addErrorFrequencyPages(document, errorFrequency);

            document.save(outputPath.toFile());
        }
    }

    private void addSummaryPage(PDDocument document, TelemetrySession session, int total, int successful, int failed, int errors, int executions, double average, long activeTime, long idleTime) throws IOException {
        PDPage page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);

        try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
            float y = TOP;

            write(stream, "Telemetry Analytics Report", LEFT, y, 18, true);
            y -= 45;

            write(stream, "Session Summary", LEFT, y, 14, true);
            y -= 30;

            write(stream, "Session started: " + formatTime(session.getStarted()), LEFT, y, 10, false);
            y -= 20;

            write(stream, "Session ended: " + formatTime(session.getEnded()), LEFT, y, 10, false);
            y -= 20;

            write(stream, "Session duration: " + session.getSessionDuration() + " seconds", LEFT, y, 10, false);
            y -= 45;

            write(stream, "Activity Metrics", LEFT, y, 14, true);
            y -= 30;

            y = writeMetric(stream, "Total compilations", total, y);
            y = writeMetric(stream, "Successful compilations", successful, y);
            y = writeMetric(stream, "Failed compilations", failed, y);
            y = writeMetric(stream, "Compilation errors", errors, y);
            y = writeMetric(stream, "Total executions", executions, y);
            y = writeMetric(stream, "Active time (seconds)", activeTime, y);
            y = writeMetric(stream, "Idle time (seconds)", idleTime, y);

            write(stream, "Average time between compilations: " + String.format("%.2f seconds", average), LEFT, y, 10, false);
            y -= 55;

            write(stream, "Compilation Results", LEFT, y, 14, true);
            y -= 35;

            drawBar(stream, "Successful", successful, total, LEFT, y);
            y -= 45;

            drawBar(stream, "Failed", failed, total, LEFT, y);
            y -= 65;

            write(stream, "Report generated from BlueJ telemetry events.", LEFT, y, 9, false);
        }
    }

    private float writeMetric(PDPageContentStream stream, String label, long value, float y) throws IOException {
        write(stream, label, LEFT, y, 10, false);
        write(stream, String.valueOf(value), 390, y, 10, false);

        return y - 25;
    }

    private void addTimelinePages(PDDocument document,List<TelemetryEvent> events) throws IOException {
        ArrayList<TelemetryEvent> sortedEvents = new ArrayList<>();

        for (TelemetryEvent event: events) {
            if (event != null) {
                sortedEvents.add(event);
            }
        }

        sortedEvents.sort(Comparator.comparing(TelemetryEvent::getTime,Comparator.nullsLast(Comparator.naturalOrder())));

        PDPage page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);

        try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
            float y = TOP;

            write(stream, "Telemetry Event Timeline",LEFT, y, 18, true);
                
            y -= 45;

            write(stream, "Timestamp", LEFT, y, 10, true);
            write(stream, "Event", 205, y, 10, true);
            write(stream, "Project", 390, y, 10, true);

            y -= 15;

            stream.moveTo(LEFT, y);
            stream.lineTo(560, y);
            stream.stroke();

            y -= 25;

            if (sortedEvents.isEmpty()) {
                write(stream, "No telemetry events were recorded.",LEFT, y, 10, false);
            }

            int displayedEvents = Math.min(sortedEvents.size(), 30);

            for (int index= 0; index < displayedEvents; index++) {
                TelemetryEvent event = sortedEvents.get(index);

                write(stream,clip(formatTime(event.getTime()), 28), LEFT, y, 8, false);

                write(stream,clip(event.getEventType(), 30), 205, y, 8, false);

                write(stream,clip(event.getProjectName(), 25), 390, y, 8, false);

                y -= 18;
            }
            
            if (sortedEvents.size() > displayedEvents) {
                int remainingEvents = sortedEvents.size() - displayedEvents;

                write(stream,remainingEvents + " additional events not shown.",LEFT, y - 15, 9, false);
            }
        }  
    }

    private void addErrorFrequencyPages(PDDocument document, Map<String, Integer> errorFrequency) throws IOException {
        ArrayList<String> errorMessages = new ArrayList<>(errorFrequency.keySet());

        errorMessages.sort(String::compareToIgnoreCase);

        PDPage page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);

        try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
            float y = TOP;

            write(stream, "Compilation Error Frequency", LEFT, y, 18, true);
            y -= 45;

            write(stream, "Error Message", LEFT, y, 11, true);
            write(stream, "Count", 490, y, 11, true);

            y -= 30;

            if (errorMessages.isEmpty()) {
                write(stream, "No compilation errors recorded.", LEFT, y, 10, false);
            }

            int displayedErrors = Math.min(errorMessages.size(), 24);

            for (int index = 0; index<displayedErrors; index++) {
                String message = errorMessages.get(index);
                int count = errorFrequency.get(message);

                write(stream, clip(message, 65), LEFT, y, 9, false);
                write(stream, String.valueOf(count), 490, y, 9, false);

                y -= 22;
            }

            if (errorMessages.size() > displayedErrors) {
                int remainingErrors = errorMessages.size() - displayedErrors;

                write(stream, remainingErrors+" additional error types not shown.", LEFT, y - 15, 9, false);
            }
        } 
    }

    private void drawBar(PDPageContentStream stream, String label, int value, int total, float x, float y) throws IOException {
        write(stream, label + ": " + value, x, y, 10, false);

        float barX = x + 115;
        float barY = y - 3;
        float maxWidth = 280;

        stream.addRect(barX, barY, maxWidth, 12);
        stream.stroke();

        float width = 0;

        if (total > 0) {
            width = maxWidth * value/(float)total;
        }

        if (width > 0) {
            stream.addRect(barX, barY, width, 12);
            stream.fill();
        }
    }

    private void write(PDPageContentStream stream, String text, float x, float y, int fontSize, boolean bold) throws IOException {
        Standard14Fonts.FontName fontName = Standard14Fonts.FontName.HELVETICA;

        if(bold) {
            fontName = Standard14Fonts.FontName.HELVETICA_BOLD;
        }

        PDType1Font font = new PDType1Font(fontName);

        stream.beginText();
        stream.setFont(font, fontSize);
        stream.newLineAtOffset(x, y);

        if (text == null) {
            text = "";
        }

        stream.showText(text);
        stream.endText();
    }

    private String formatTime(LocalDateTime time) {
        if (time == null) {
            return "Not recorded";
        }

        return time.format(TIME_FORMAT);
    }

    private String clip(String text, int maxLength) {
        if (text == null) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength - 3) + "...";
    }
}