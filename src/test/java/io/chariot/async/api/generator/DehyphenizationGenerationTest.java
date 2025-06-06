package io.chariot.async.api.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static utils.TestUtils.randomPackageName;

class DehyphenizationGenerationTest {

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
    @EnabledOnOs(OS.WINDOWS)
    void shouldGenerateClassesWithDehyphenizedNames() throws Exception {
        var packageName = "io.chariot.some_package." + randomPackageName();
        var args = new String[] {
            "specs/dehyphenization-spec.yaml",
            outputDir.toString(),
            packageName
        };

        AsyncApiGenerator.main(args);

        // assert
        var targetPath = outputDir.resolve(packageName.replace(".", File.separator)).toString();
        var wrongFile = Path.of(targetPath)
                .resolve("messages/Create-extMessage.java")
                .toFile();
        assertFalse(wrongFile.exists() || wrongFile.isDirectory());

        var goodFile = Path.of(targetPath)
                .resolve("messages/CreateExtMessage.java");
        assertTrue(goodFile.toFile().exists());
        var goodFileContent = Files.readString(goodFile);
        assertFalse(goodFileContent.contains("public class Create-extMessage {"));
        assertTrue(goodFileContent.contains("public class CreateExtMessage {"));

        var classFile = Path.of(targetPath)
                .resolve("dto/ResourceObject.java")
                .toFile();
        var classContainer1 = Path.of(targetPath)
                .resolve("dto/ObjectTypeDTO.java")
                .toFile();

        var classContainer2 = Path.of(targetPath)
                .resolve("dto/ObjectTypeDto2.java")
                .toFile();

        checkFileContains(classFile, Set.of(
                "public class ResourceObject {"
        ), false);
        // Check that field with ObjectTypeDTO type exists, regardless of name
        assertTrue(Files.readString(Path.of(classFile.getPath())).contains("ObjectTypeDTO"));
        checkFileContains(classContainer1, Set.of(
                "public class ObjectTypeDTO {",
                "public static ObjectTypeDTOBuilder builder() {"
        ), true);

        checkFileContains(classContainer2, Set.of(
                "public class ObjectTypeDTO2 {",
                "public static ObjectTypeDTO2Builder builder() {"
        ), true);
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void shouldHandleVariousDehyphenizationPatterns() throws Exception {
        var packageName = "io.chariot.some_package." + randomPackageName();
        var args = new String[] {
            "specs/dehyphenization-spec.yaml",
            outputDir.toString(),
            packageName
        };

        AsyncApiGenerator.main(args);

        // assert
        var targetPath = outputDir.resolve(packageName.replace(".", File.separator)).toString();
        
        // Test various dehyphenization patterns
        var testFile = Path.of(targetPath)
                .resolve("messages/CreateExtMessage.java");
        assertTrue(testFile.toFile().exists());
        
        var testFileContent = Files.readString(testFile);
        // Check that class names are properly dehyphenized
        assertTrue(testFileContent.contains("public class CreateExtMessage {"));
        
        // Check that field names are properly dehyphenized
        var classFile = Path.of(targetPath)
                .resolve("dto/ResourceObject.java")
                .toFile();
        assertTrue(classFile.exists());
        
        var classContainer = Path.of(targetPath)
                .resolve("dto/ObjectTypeDTO.java")
                .toFile();
        assertTrue(classContainer.exists());
    }

    private void checkFileContains(File file, Set<String> strings, boolean ignoreCase) {
        var str = new HashSet<>(strings);
        try(var lineStream = Files.lines(file.toPath())) {
            lineStream
                .takeWhile(ignored -> !str.isEmpty())
                .forEach(line -> {
                    var foundStr = new ArrayList<String>();
                    str.forEach(s -> {
                        if(ignoreCase && org.gradle.internal.impldep.org.apache.commons.lang3.StringUtils.containsIgnoreCase(line, s) || line.contains(s)
                            || patternMatches(s, line)
                        ) {
                            foundStr.add(s);
                        }
                    });
                    foundStr.forEach(str::remove);
                });
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
        if(!str.isEmpty()) {
            throw new IllegalStateException("Strings not found in file '%s':\n%s\n".formatted(
                    file.getAbsolutePath(),
                    String.join("\n", str)
            ));
        }
    }

    private boolean patternMatches(String s, String line) {
        try {
            return Pattern.matches(s, line);
        } catch (PatternSyntaxException exc) {
            // Will appear when comparing strings as is, because regex syntax may not be followed in
            // arbitrary string we're looking for in generated file
            return false;
        }
    }
}