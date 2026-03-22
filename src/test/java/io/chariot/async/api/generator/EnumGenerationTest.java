package io.chariot.async.api.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static utils.TestUtils.randomPackageName;

class EnumGenerationTest {

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
    void shouldGenerateEnumClassesProperly() {
        var packageName = "io.chariot.some_package." + randomPackageName();
        var args = new String[] {
            "specs/enum-generation-spec.yaml",
            outputDir.toString(),
            packageName
        };

        AsyncApiGenerator.main(args);

        // assert
        var targetPath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        var enumFile = targetPath.resolve("StateEnum.java").toFile();
        var classFile = targetPath.resolve("ModifyResourceObject.java").toFile();
        var classFile2 = targetPath.resolve("ModifyResource.java").toFile();
        
        assertTrue(enumFile.exists(), "StateEnum.java should be generated");
        assertTrue(classFile.exists(), "ModifyResourceObject.java should be generated");
        assertTrue(classFile2.exists(), "ModifyResource.java should be generated");
        
        // Check that files are not empty
        assertTrue(enumFile.length() > 0, "StateEnum.java should not be empty");
        assertTrue(classFile.length() > 0, "ModifyResourceObject.java should not be empty");
        assertTrue(classFile2.length() > 0, "ModifyResource.java should not be empty");
    }
}