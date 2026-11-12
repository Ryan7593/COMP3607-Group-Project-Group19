package comp3607project;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class PDFReportGeneratorTest {

    @Test
    public void testGenerateReportCreatesPDFFile() throws Exception {
        TelemetryCollection collection = new TelemetryCollection();
        collection.addEvent(new TelemetryEvent("Compilation_Started", "TestProject"));
        collection.addEvent(new TelemetryEvent("Compilation_Succeeded", "TestProject"));

        TelemetrySession session = new TelemetrySession();
        session.startSession();
        session.endSession();

        Path outputPath = Files.createTempFile("telemetry-report-test", ".pdf");

        PDFReportGenerator generator = new PDFReportGenerator();
        generator.generateReport(collection, session, outputPath);

        assertTrue(Files.exists(outputPath));
        assertTrue(Files.size(outputPath) > 0);

        Files.deleteIfExists(outputPath);
    }

    @Test
    public void testGenerateReportRejectsNullValues() throws Exception {
        PDFReportGenerator generator = new PDFReportGenerator();
        Path outputPath = Files.createTempFile("telemetry-report-test", ".pdf");

        assertThrows(IllegalArgumentException.class,
                () -> generator.generateReport(null, new TelemetrySession(), outputPath));

        Files.deleteIfExists(outputPath);
    }
}
