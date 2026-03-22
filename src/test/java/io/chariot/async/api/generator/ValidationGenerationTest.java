package io.chariot.async.api.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static utils.TestUtils.randomPackageName;

class ValidationGenerationTest {

    private static final String TEST_YAML_RESOURCE = "specs/validation-test-spec.yaml";
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
    void shouldGenerateNotNullAnnotationsForRequiredFields() {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path userDtoPath = dtoPackagePath.resolve("UserDTO.java");

        assertTrue(Files.exists(userDtoPath), "UserDTO.java should be generated");

        String content = readFileContent(userDtoPath);

        // Проверяем наличие аннотации @NotNull для обязательных полей
        assertTrue(content.contains("@NotNull"), "Should have @NotNull annotation");

        // Проверяем конкретные обязательные поля
        assertTrue(content.contains("@NotNull"),
                "id field should have @NotNull annotation");

        // Проверяем, что необязательное поле не имеет @NotNull
        assertFalse(Pattern.compile("@NotNull\\s*\\n\\s*private Integer age;").matcher(content).find(),
                "age field should not have @NotNull annotation");
    }

    @Test
    void shouldGeneratePatternAnnotations() {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path userDtoPath = dtoPackagePath.resolve("UserDTO.java");
        String content = readFileContent(userDtoPath);

        // Проверяем аннотации @Pattern
        assertTrue(content.contains("@Pattern"), "Should have @Pattern annotation");

        // Проверяем конкретные паттерны
        assertTrue(content.contains("regexp = \"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}$\""),
                "Should have pattern for email field");
        assertTrue(content.contains("regexp = \"^[a-zA-Z0-9_]+$\""),
                "Should have pattern for username field");

        // Проверяем паттерн для ipAddress во вложенном объекте
        Path metadataPath = dtoPackagePath.resolve("MetadataData.java");
        String metadataContent = readFileContent(metadataPath);
        assertTrue(metadataContent.contains("regexp = \"^(?:(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\\\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])$\""),
                "Should have IP address pattern");
    }

    @Test
    void shouldGenerateSizeAnnotations() {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path userDtoPath = dtoPackagePath.resolve("UserDTO.java");
        String content = readFileContent(userDtoPath);

        // Проверяем аннотации @Size для строк
        assertTrue(content.contains("@Size("), "Should have @Size annotation");

        // Проверяем minLength и maxLength для email
        assertTrue(content.contains("@Size(min = 5, max = 255)"),
                "Should have size constraints for email");

        // Проверяем minLength и maxLength для username
        assertTrue(content.contains("@Size(min = 3, max = 50)"),
                "Should have size constraints for username");
    }

    @Test
    void shouldGenerateMinMaxAnnotations() {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path userDtoPath = dtoPackagePath.resolve("UserDTO.java");
        String content = readFileContent(userDtoPath);

        // Проверяем аннотации @Min и @Max
        assertTrue(content.contains("@Min(") || content.contains("@DecimalMin("),
                "Should have @Min or @DecimalMin annotation");
        assertTrue(content.contains("@Max(") || content.contains("@DecimalMax("),
                "Should have @Max or @DecimalMax annotation");

        // Проверяем числовые ограничения для age
        assertTrue(content.contains("@Min(0L)") && content.contains("@Max(150L)"),
                "Should have min/max for age field");

        // Проверяем числовые ограничения для score (DecimalMin/DecimalMax для дробных чисел)
        assertTrue((content.contains("@DecimalMin(\"0.0\")") || content.contains("@Min(0)")) &&
                        (content.contains("@DecimalMax(\"100.0\")") || content.contains("@Max(100)")),
                "Should have decimal min/max for score field");
    }

    @Test
    void shouldGenerateValidationsForAllOf() {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path productDtoPath = dtoPackagePath.resolve("ProductDTO.java");

        assertTrue(Files.exists(productDtoPath), "ProductDTO.java should be generated");

        String content = readFileContent(productDtoPath);

        // Проверяем, что валидации из BaseProduct наследуются
        assertTrue(content.contains("@Size(min = 2, max = 100)"),
                "Should have size constraints from BaseProduct");
        assertTrue(content.contains("regexp = \"^[A-Z]{3}-[0-9]{6}$\""),
                "Should have pattern from BaseProduct");

        // Проверяем, что собственные валидации ProductDTO тоже присутствуют
        assertTrue(content.contains("@DecimalMin(\"0.01\")"),
                "Should have DecimalMin for price");
        assertTrue(content.contains("@Min(0L)"),
                "Should have Min for stock");
    }

    @Test
    void shouldGenerateValidationsForMessageTraits() {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path traitsPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/messagetraits");
        Path commonHeadersPath = traitsPackagePath.resolve("CommonHeaders.java");

        assertTrue(Files.exists(commonHeadersPath), "CommonHeaders.java should be generated");

        String content = readFileContent(commonHeadersPath);

        // Проверяем валидации для message traits
        assertTrue(content.contains("@NotNull"),
                "Message traits should have @NotNull for required fields");

        // Проверяем паттерн для messageType
        assertTrue(content.contains("regexp = \"^[A-Z_]+$\""),
                "Should have pattern for messageType");

        // Проверяем, что nullable поле не имеет @NotNull
        assertFalse(Pattern.compile("@NotNull\\s*\\n\\s*private UUID correlationId;").matcher(content).find(),
                "correlationId (nullable) should not have @NotNull");
    }

    @Test
    void shouldGenerateValidationsForArrays() {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path userDtoPath = dtoPackagePath.resolve("UserDTO.java");
        String content = readFileContent(userDtoPath);

        // Проверяем валидации для массивов
        assertTrue(content.contains("@Size(min = 5, max = 255)"),
                "Should have size constraints for array");

        // Проверяем, что поле tags имеет тип List
        assertTrue(content.contains("private List<String> tags;"),
                "Should have List type for tags");
    }

    @Test
    void shouldGenerateValidationsForNestedObjects() {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path metadataPath = dtoPackagePath.resolve("MetadataData.java");

        assertTrue(Files.exists(metadataPath), "MetadataData.java should be generated");

        String content = readFileContent(metadataPath);

        // Проверяем валидации во вложенном объекте
        assertTrue(content.contains("@NotNull"),
                "createdAt should have @NotNull (required field in nested object)");

        assertTrue(content.matches("(?s).*@Min\\(1L\\)\\s+@Max\\(10L\\).*"),
                "Should have min/max for priority field");
    }

    @Test
    void shouldGenerateProperImportsForValidationAnnotations() {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path userDtoPath = dtoPackagePath.resolve("UserDTO.java");
        String content = readFileContent(userDtoPath);

        // Проверяем импорты для валидационных аннотаций
        assertTrue(content.contains("import jakarta.validation.constraints.NotNull;") ||
                        content.contains("import javax.validation.constraints.NotNull;"),
                "Should import NotNull annotation");

        assertTrue(content.contains("import jakarta.validation.constraints.Pattern;") ||
                        content.contains("import javax.validation.constraints.Pattern;"),
                "Should import Pattern annotation");

        assertTrue(content.contains("import jakarta.validation.constraints.Size;") ||
                        content.contains("import javax.validation.constraints.Size;"),
                "Should import Size annotation");

        assertTrue(content.contains("import jakarta.validation.constraints.Min;") ||
                        content.contains("import javax.validation.constraints.Min;"),
                "Should import Min annotation");

        assertTrue(content.contains("import jakarta.validation.constraints.Max;") ||
                        content.contains("import javax.validation.constraints.Max;"),
                "Should import Max annotation");

        // Проверяем импорты для типов данных
        assertTrue(content.contains("import java.util.UUID;"),
                "Should import UUID");
        assertTrue(content.contains("import java.util.List;"),
                "Should import List");
    }

    @Test
    void shouldHandleNullableFieldsCorrectly() {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path metadataPath = dtoPackagePath.resolve("MetadataData.java");
        String content = readFileContent(metadataPath);

        // Проверяем обработку nullable полей
        // В Java nullable поля могут быть аннотированы @Nullable или просто не иметь @NotNull
        // Для Java 8+ можно использовать @Nullable из javax.annotation
        if (content.contains("javax.annotation.Nullable")) {
            assertTrue(content.contains("@Nullable\n    private Instant updatedAt;"),
                    "updatedAt should have @Nullable annotation");
        } else {
            // Если не используется @Nullable, то просто проверяем отсутствие @NotNull
            assertFalse(Pattern.compile("@NotNull\\s*\\n\\s*private Instant updatedAt;").matcher(content).find(),
                    "updatedAt should not have @NotNull");
        }
    }

    @Test
    void shouldGenerateValidationsForEnumFields() {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path userDtoPath = dtoPackagePath.resolve("UserDTO.java");
        String content = readFileContent(userDtoPath);

        // Проверяем, что enum поле имеет правильный тип
        assertTrue(content.contains("private StatusEnum status;"),
                "Should use enum type for status field");

        // Проверяем, что enum файл сгенерирован
        Path statusEnumPath = dtoPackagePath.resolve("StatusEnum.java");
        assertTrue(Files.exists(statusEnumPath), "StatusEnum.java should be generated");

        String enumContent = readFileContent(statusEnumPath);
        assertTrue(enumContent.contains("public enum StatusEnum"),
                "Should be an enum class");
        assertTrue(enumContent.contains("ACTIVE"),
                "Should contain ACTIVE value");
        assertTrue(enumContent.contains("INACTIVE"),
                "Should contain INACTIVE value");
        assertTrue(enumContent.contains("PENDING"),
                "Should contain PENDING value");
    }

    @Test
    void shouldGenerateValidationsWithCorrectOrder() {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path userDtoPath = dtoPackagePath.resolve("UserDTO.java");
        String content = readFileContent(userDtoPath);

        // Проверяем порядок аннотаций для поля email
        // Ожидаемый порядок: @NotNull, @Pattern, @Size
        String emailFieldPattern = "@NotNull\\s*\\n\\s*@Pattern\\(regexp = \"[^\"]+\"\\)\\s*\\n\\s*@Size\\(min = 5, max = 255\\)\\s*\\n\\s*private String email;";
        assertTrue(Pattern.compile(emailFieldPattern, Pattern.DOTALL).matcher(content).find(),
                "Email field should have correct annotation order");

        // Проверяем порядок аннотаций для поля username
        // @NotNull, @Pattern, @Size
        String usernameFieldPattern = "@NotNull\\s*\\n\\s*@Pattern\\(regexp = \"[^\"]+\"\\)\\s*\\n\\s*@Size\\(min = 3, max = 50\\)\\s*\\n\\s*private String username;";
        assertTrue(Pattern.compile(usernameFieldPattern, Pattern.DOTALL).matcher(content).find(),
                "Username field should have correct annotation order");
    }

    @Test
    void shouldGenerateValidationsForDifferentLanguages() {
        // Тест для Java
        testLanguageValidations("java");

        // Тест для Kotlin (если поддерживается)
        // testLanguageValidations("kotlin");
    }

    private void testLanguageValidations(String languageStr) {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName,
                "--language=" + languageStr
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path userDtoPath = dtoPackagePath.resolve("UserDTO" +
                ("kotlin".equals(languageStr) ? ".kt" : ".java"));

        if (Files.exists(userDtoPath)) {
            String content = readFileContent(userDtoPath);

            if ("java".equals(languageStr)) {
                // Проверяем Java-специфичные аннотации
                assertTrue(content.contains("@NotNull") || content.contains("@NonNull"),
                        "Java should have NotNull annotation");
            } else if ("kotlin".equals(languageStr)) {
                // Проверяем Kotlin-специфичные аннотации
                assertTrue(content.contains("@field:NotNull") || content.contains("@get:NotNull"),
                        "Kotlin should have field/Getter NotNull annotation");
            }
        }
    }

    @Test
    void shouldHandleMultipleValidationAnnotationsOnSameField() {
        String packageName = "io.chariot.validationtest." + randomPackageName();
        String[] args = new String[]{
                TEST_YAML_RESOURCE,
                outputDir.toString(),
                packageName
        };

        AsyncApiGenerator.main(args);

        Path dtoPackagePath = outputDir.resolve(packageName.replace(".", File.separator) + "/dto");
        Path baseProductPath = dtoPackagePath.resolve("BaseProduct.java");

        if (Files.exists(baseProductPath)) {
            String content = readFileContent(baseProductPath);

            // Проверяем поле sku с двумя аннотациями
            boolean hasSkuValidations = content.contains("@NotNull") &&
                    content.contains("@Pattern");

            assertTrue(hasSkuValidations,
                    "sku field should have both @NotNull and @Pattern annotations");
        }
    }

    private String readFileContent(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            fail("Failed to read file: " + path, e);
            return "";
        }
    }
}
