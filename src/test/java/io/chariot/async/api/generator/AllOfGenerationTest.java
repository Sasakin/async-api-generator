package io.chariot.async.api.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static utils.TestUtils.randomPackageName;

class AllOfGenerationTest {

    private static final String TEST_YAML_RESOURCE = "specs/allof-dto-spec.yaml";
    private static final String OUTPUT_DIR_NAME = "build/src/main/java";

    @TempDir
    Path tempDir;

    private Path outputDir;

    @BeforeEach
    void setUp() throws IOException {
        outputDir = tempDir.resolve(OUTPUT_DIR_NAME);
        Files.createDirectories(outputDir);
    }

    @Test
    void shouldGenerateClassWithAllOfInheritance() {
        String packageName = "io.chariot.alloftest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        // Проверяем базовые классы
        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");

        // 1. Проверяем BaseResource
        Path baseResourcePath = dtoPackagePath.resolve("BaseResource.java");
        assertTrue(Files.exists(baseResourcePath), "BaseResource.java should be generated");
        String baseContent = readFileContent(baseResourcePath);
        verifyBaseResourceContent(baseContent);

        // 2. Проверяем AwsProperties
        Path awsPropertiesPath = dtoPackagePath.resolve("AwsProperties.java");
        assertTrue(Files.exists(awsPropertiesPath), "AwsProperties.java should be generated");

        // 3. Проверяем AwsResource (комбинация через allOf)
        Path awsResourcePath = dtoPackagePath.resolve("AwsResource.java");
        assertTrue(Files.exists(awsResourcePath), "AwsResource.java should be generated");
        String awsResourceContent = readFileContent(awsResourcePath);
        verifyAwsResourceContent(awsResourceContent, packageName);

        // 4. Проверяем HybridResource (inline allOf)
        Path hybridResourcePath = dtoPackagePath.resolve("HybridResource.java");
        assertTrue(Files.exists(hybridResourcePath), "HybridResource.java should be generated");
        String hybridContent = readFileContent(hybridResourcePath);
        verifyHybridResourceContent(hybridContent, packageName);

        // 5. Проверяем ExtendedAwsResource (многоуровневый allOf)
        Path extendedAwsResourcePath = dtoPackagePath.resolve("ExtendedAwsResource.java");
        assertTrue(Files.exists(extendedAwsResourcePath), "ExtendedAwsResource.java should be generated");
        String extendedContent = readFileContent(extendedAwsResourcePath);
        verifyExtendedAwsResourceContent(extendedContent, packageName);

        // 6. Проверяем CompleteResource (allOf с дополнительными свойствами)
        Path completeResourcePath = dtoPackagePath.resolve("CompleteResource.java");
        assertTrue(Files.exists(completeResourcePath), "CompleteResource.java should be generated");
        String completeContent = readFileContent(completeResourcePath);
        verifyCompleteResourceContent(completeContent, packageName);
    }

    @Test
    void shouldCombinePropertiesFromAllOf() {
        String packageName = "io.chariot.alloftest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path awsResourcePath = dtoPackagePath.resolve("AwsResource.java");
        String content = readFileContent(awsResourcePath);

        // Проверяем, что AwsResource содержит поля из BaseResource
        assertTrue(content.contains("private UUID id;"), "Should have id field from BaseResource");
        assertTrue(content.contains("private String name;"), "Should have name field from BaseResource");
        assertTrue(content.contains("private String description;"), "Should have description field from BaseResource");
        assertTrue(content.contains("private String createdBy;"), "Should have createdBy field from BaseResource");

        // Проверяем, что AwsResource содержит поля из AwsProperties
        assertTrue(content.contains("private RegionEnum region;"), "Should have region field from AwsProperties");
        assertTrue(content.contains("private String instanceType;"), "Should have instanceType field from AwsProperties");
        assertTrue(content.contains("private String vpcId;"), "Should have vpcId field from AwsProperties");
        assertTrue(content.contains("private List<String> subnetIds;"), "Should have subnetIds field from AwsProperties");

        // Проверяем, что AwsResource содержит собственные поля
        assertTrue(content.contains("private List<String> tags;"), "Should have tags field from AwsResource itself");
    }

    @Test
    void shouldHandleRequiredFieldsFromAllOf() {
        String packageName = "io.chariot.alloftest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path awsResourcePath = dtoPackagePath.resolve("AwsResource.java");
        String content = readFileContent(awsResourcePath);

        // Проверяем, что required поля из BaseResource присутствуют
        assertTrue(content.contains("@NotNull"), "Should have @NotNull annotation for required fields");

        // Проверяем наличие аннотаций для полей id и name (required из BaseResource)
        assertTrue(Pattern.compile("\\s*@NotNull\\s*\\n\\s*private UUID id;").matcher(content).find(),
                "id field should have @NotNull annotation");
    }

    @Test
    void shouldGenerateEnumsFromAllOfSchemas() {
        String packageName = "io.chariot.alloftest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");

        // Проверяем enum для region в AwsProperties
        Path regionEnumPath = dtoPackagePath.resolve("RegionEnum.java");
        assertTrue(Files.exists(regionEnumPath), "RegionEnum.java should be generated");
        String regionEnumContent = readFileContent(regionEnumPath);
        assertTrue(regionEnumContent.contains("public enum RegionEnum"), "Should be an enum");
        assertTrue(regionEnumContent.contains("US_EAST_1"), "Should contain US_EAST_1 value");
        assertTrue(regionEnumContent.contains("US_WEST_2"), "Should contain US_WEST_2 value");
        assertTrue(regionEnumContent.contains("EU_WEST_1"), "Should contain EU_WEST_1 value");
    }

    @Test
    void shouldHandlePatternConstraintsFromAllOf() {
        String packageName = "io.chariot.alloftest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path awsPropertiesPath = dtoPackagePath.resolve("AwsProperties.java");
        String content = readFileContent(awsPropertiesPath);

        // Проверяем аннотации @Pattern для полей с регулярными выражениями
        assertTrue(content.contains("@Pattern"), "Should have @Pattern annotation for fields with pattern");

        // Проверяем конкретные паттерны
        assertTrue(content.contains("regexp = \"^[a-z0-9]+\\\\.[a-z0-9]+$\""),
                "Should have pattern for instanceType field");
        assertTrue(content.contains("regexp = \"^vpc-[a-z0-9]+$\""),
                "Should have pattern for vpcId field");
    }

    @Test
    void shouldGenerateNestedObjectsInAllOf() {
        String packageName = "io.chariot.alloftest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");

        // Проверяем вложенный объект monitoring в ExtendedAwsResource
        Path monitoringPath = dtoPackagePath.resolve("MonitoringData.java");
        assertTrue(Files.exists(monitoringPath), "MonitoringData.java should be generated for nested object");

        // Проверяем вложенный объект hybridConfig в HybridResource
        Path hybridConfigPath = dtoPackagePath.resolve("HybridConfigData.java");
        assertTrue(Files.exists(hybridConfigPath), "HybridConfigData.java should be generated for nested object");
    }

    private String readFileContent(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            fail("Failed to read file: " + path, e);
            return "";
        }
    }

    private void verifyBaseResourceContent(String content) {
        assertTrue(content.contains("public class BaseResource {"));
        assertTrue(content.contains("private UUID id;"));
        assertTrue(content.contains("private String name;"));
        assertTrue(content.contains("private String description;"));
        assertTrue(content.contains("private String createdBy;"));
        assertTrue(content.contains("import java.util.UUID;"));
    }

    private void verifyAwsResourceContent(String content, String packageName) {
        assertTrue(content.contains("public class AwsResource {"));

        // Поля из BaseResource
        assertTrue(content.contains("private UUID id;"));
        assertTrue(content.contains("private String name;"));

        // Поля из AwsProperties
        assertTrue(content.contains("private RegionEnum region;"));
        assertTrue(content.contains("private String instanceType;"));

        // Собственные поля
        assertTrue(content.contains("private List<String> tags;"));

        // Импорты
        assertTrue(content.contains("import java.util.List;"));
    }

    private void verifyHybridResourceContent(String content, String packageName) {
        assertTrue(content.contains("public class HybridResource {"));

        // Поля из BaseResource
        assertTrue(content.contains("private UUID id;"));

        // Собственные поля
        assertTrue(content.contains("private CloudProviderEnum cloudProvider;"));
        assertTrue(content.contains("private HybridConfigData hybridConfig;"));
    }

    private void verifyExtendedAwsResourceContent(String content, String packageName) {
        assertTrue(content.contains("public class ExtendedAwsResource {"));

        // Поля из AwsResource (которые включают BaseResource и AwsProperties)
        assertTrue(content.contains("private UUID id;"));
        assertTrue(content.contains("private String instanceType;"));
        assertTrue(content.contains("private List<String> tags;"));

        // Дополнительные поля
        assertTrue(content.contains("private MonitoringData monitoring;"));
        assertTrue(content.contains("private String costCenter;"));
    }

    private void verifyCompleteResourceContent(String content, String packageName) {
        assertTrue(content.contains("public class CompleteResource {"));

        // Поля из BaseResource
        assertTrue(content.contains("private UUID id;"));
        assertTrue(content.contains("private String name;"));

        // Собственные поля
        assertTrue(content.contains("private StatusEnum status;"));
        assertTrue(content.contains("private MetadataData metadata;"));
    }
}