package comp3607project;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class JSONExporterTest {

    @Test
    public void testExportCreatesJSONFile() throws Exception {
        TelemetryCollection collection = new TelemetryCollection();

        TelemetryEvent compilation = new TelemetryEvent("Compilation_Started", "TestProject");
        collection.addEvent(compilation);

        TelemetryEvent error = new TelemetryEvent("Compilation_Error", "TestProject");
        error.setErrorMessage("Missing semicolon");
        error.setErrorLineNumber(10);
        error.setErrorFileName("TestClass.java");
        collection.addEvent(error);

        TelemetrySession session = new TelemetrySession();
        session.startSession();
        session.endSession();

        Path outputPath = Files.createTempFile("telemetry-test", ".json");

        JSONExporter exporter = new JSONExporter();
        exporter.export(collection, session, outputPath);

        String json = Files.readString(outputPath);

        assertTrue(Files.exists(outputPath));
        assertTrue(json.contains("session_metadata"));
        assertTrue(json.contains("analytics"));
        assertTrue(json.contains("Compilation_Error"));
        assertTrue(json.contains("Missing semicolon"));
        assertTrue(json.contains("TestClass.java"));

        Files.deleteIfExists(outputPath);
    }

    @Test
    public void testExportRejectsNullValues() throws Exception {
        JSONExporter exporter = new JSONExporter();
        Path outputPath = Files.createTempFile("telemetry-test", ".json");

        assertThrows(IllegalArgumentException.class,
                () -> exporter.export(null, new TelemetrySession(), outputPath));

        Files.deleteIfExists(outputPath);
    }
}
