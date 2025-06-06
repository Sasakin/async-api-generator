package io.chariot.async.api.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static utils.TestUtils.randomPackageName;

class DataFormatsGenerationTest {

    private static final String OUTPUT_DIR_NAME = "build/src/main/java";

    @TempDir
    Path tempDir;

    private Path outputDir;

    @BeforeEach
    void setUp() throws Exception {
        // Create directory structure as in original tests
        outputDir = tempDir.resolve(OUTPUT_DIR_NAME);
        Files.createDirectories(outputDir);
    }

    @Test
    void shouldGenerateClassesWithDataFormats() {
        var packageName = "io.chariot.some_package." + randomPackageName();
        var args = new String[] {
            "specs/data-formats-spec.yaml",
            outputDir.toString(),
            packageName
        };

        AsyncApiGenerator.main(args);

        // assert
        var targetPath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        var classFile = targetPath.resolve("FormatTestDTO.java").toFile();
        
        assertTrue(classFile.exists(), "FormatTestDTO.java should be generated");
        
        // Check that file is not empty
        assertTrue(classFile.length() > 0, "FormatTestDTO.java should not be empty");
    }
}