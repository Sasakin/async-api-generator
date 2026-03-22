package io.chariot.async.api.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static utils.TestUtils.randomPackageName;

class BasicDtoGenerationTest {

    private static final String TEST_YAML_RESOURCE = "specs/basic-dto-generation-spec.yaml";
    private static final String BASE_PACKAGE = "io.project.api";
    private static final String OUTPUT_DIR_NAME = "build/src/main/java";

    @TempDir
    Path tempDir;

    private Path outputDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create directory structure
        outputDir = tempDir.resolve(OUTPUT_DIR_NAME);
        Files.createDirectories(outputDir);
    }

    @Test
    void shouldGenerateBasicDtoClassesFromSpec() {
        // Prepare arguments as in the assignment
        String[] args = new String[] {
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                BASE_PACKAGE
        };

        // Run generator
        AsyncApiGenerator.main(args);

        // Check DTO package creation
        Path dtoPackagePath = outputDir.resolve(BASE_PACKAGE.replace(".", File.separator) + "/dto");
        assertTrue(Files.exists(dtoPackagePath), "DTO package directory should exist");

        // Check ReferenceDTO.java creation
        Path referenceDtoPath = dtoPackagePath.resolve("ReferenceDTO.java");
        assertTrue(Files.exists(referenceDtoPath), "ReferenceDTO.java should be generated");

        // Check ReferenceDTO.java content
        String fileContent = readFileContent(referenceDtoPath);
        verifyReferenceDtoContent(fileContent);

        // Check StatusDTO.java creation
        Path statusDtoPath = dtoPackagePath.resolve("StatusDTO.java");
        assertTrue(Files.exists(statusDtoPath), "StatusDTO.java should be generated");

        // Check StatusDTO.java content
        fileContent = readFileContent(statusDtoPath);
        verifyStatusDtoContent(fileContent);

        // Check enum creation for type field
        Path typeEnumPath = dtoPackagePath.resolve("TypeEnum.java");
        assertTrue(Files.exists(typeEnumPath), "TypeEnum.java should be generated");

        // Check TypeEnum.java content
        fileContent = readFileContent(typeEnumPath);
        verifyTypeEnumContent(fileContent);
    }

    @Test
    void shouldGenerateNestedObjectsProperly() {
        // Prepare arguments
        String[] args = new String[] {
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                BASE_PACKAGE
        };

        // Run generator
        AsyncApiGenerator.main(args);

        // Check nested object MetadataData creation
        Path dtoPackagePath = outputDir.resolve(BASE_PACKAGE.replace(".", File.separator) + "/dto");
        Path metadataPath = dtoPackagePath.resolve("MetadataData.java");
        assertTrue(Files.exists(metadataPath), "MetadataData.java should be generated");

        // Check MetadataData.java content
        String metadataContent = readFileContent(metadataPath);
        assertTrue(metadataContent.contains("public class MetadataData {"),
                "Should declare MetadataData class");
        assertTrue(metadataContent.contains("private String region;"),
                "Should have region field");
        assertTrue(metadataContent.contains("private String zone;"),
                "Should have zone field");
    }

    private String readFileContent(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            fail("Failed to read file: " + path, e);
            return "";
        }
    }

    private void verifyReferenceDtoContent(String content) {
        assertTrue(content.contains("package io.project.api.dto;"),
                "Should have correct package declaration");
        assertTrue(content.contains("@Data"), "Should contain Lombok @Data annotation");
        assertTrue(content.contains("public class ReferenceDTO {"), "Should declare ReferenceDTO class");

        // Check fields
        assertTrue(content.contains("private UUID id;"), "Should have UUID id field");
        assertTrue(content.contains("private TypeEnum type;"), "Should have TypeEnum type field");
        assertTrue(content.contains("private String name;"), "Should have String name field");
        assertTrue(content.contains("private String description;"), "Should have String description field");
        assertTrue(content.contains("private MetadataData metadata;"), "Should have nested MetadataData");
        assertTrue(content.contains("private List<String> tags;"), "Should have List<String> tags field");

        // Check builder
        assertTrue(content.contains("public static ReferenceDTOBuilder builder()"),
                "Should have builder method");
        assertTrue(content.contains("public static class ReferenceDTOBuilder"),
                "Should have builder class");
    }

    private void verifyStatusDtoContent(String content) {
        assertTrue(content.contains("public class StatusDTO {"), "Should declare StatusDTO class");

        // Check fields
        assertTrue(content.contains("private StateEnum state;"), "Should have StateEnum state field");
        assertTrue(content.contains("private Instant lastUpdate;"), "Should have Instant lastUpdate field");
        assertTrue(content.contains("private Integer health;"), "Should have Integer health field");

        // Check imports
        assertTrue(content.contains("import java.time.Instant;"), "Should import Instant");
    }

    private void verifyTypeEnumContent(String content) {
        assertTrue(content.contains("public enum TypeEnum {"), "Should be an enum");
        assertTrue(content.contains("USER"), "Should contain USER value");
        assertTrue(content.contains("SYSTEM"), "Should contain SYSTEM value");
    }
}