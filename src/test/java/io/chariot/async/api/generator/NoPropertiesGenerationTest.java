package io.chariot.async.api.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static utils.TestUtils.randomPackageName;

class NoPropertiesGenerationTest {

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
    void shouldGenerateCodeFromSpecWithoutProperties() {
        var packageName = "io.chariot.some_package." + randomPackageName();
        var args = new String[] {
            "specs/no-properties-spec.yaml",
            outputDir.toString(),
            packageName
        };

        AsyncApiGenerator.main(args);

        // assert
        var targetPath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        var resourceCreateFile = targetPath.resolve("ResourceCreate.java").toFile();
        var configCreateFile = targetPath.resolve("ConfigCreate.java").toFile();

        assertTrue(resourceCreateFile.exists(), "ResourceCreate.java should be generated");
        assertTrue(configCreateFile.exists(), "ConfigCreate.java should be generated");
        
        // Check that files are not empty
        assertTrue(resourceCreateFile.length() > 0, "ResourceCreate.java should not be empty");
        assertTrue(configCreateFile.length() > 0, "ConfigCreate.java should not be empty");
    }
}