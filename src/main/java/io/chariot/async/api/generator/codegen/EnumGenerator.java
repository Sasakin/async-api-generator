package io.chariot.async.api.generator.codegen;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.utils.FileUtils;
import io.chariot.async.api.generator.utils.GeneratorHelper;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.chariot.async.api.generator.utils.GeneratorHelper.dehyphenizeClassName;

@RequiredArgsConstructor
public class EnumGenerator {
    private final String basePackage;
    private final TargetLanguage targetLanguage;
    private final Configuration freemarkerConfig;

    public EnumGenerator(String basePackage, TargetLanguage targetLanguage) {
        this.basePackage = basePackage;
        this.targetLanguage = targetLanguage;
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        freemarkerConfig.setClassForTemplateLoading(EnumGenerator.class, "/templates");
    }

    public void generate(String className, List<Object> enumValues, File outputDir, String packagePath)
            throws IOException, TemplateException {

        if (enumValues.isEmpty()) return;

        // Определяем тип первого элемента
        boolean isNumeric = isNumeric(enumValues.get(0));

        // Проверяем, что все элементы совместимы
        for (Object value : enumValues) {
            if (isNumeric(value) != isNumeric) {
                throw new IllegalArgumentException("Все значения enum должны быть одного типа (числа или строки). Несоответствие: " + value);
            }
        }

        // Подготовка данных
        Map<String, Object> data = createTemplateData(className, enumValues, isNumeric, packagePath);

        // Загрузка шаблона
        Template template = freemarkerConfig.getTemplate(
                targetLanguage.getTemplatePath("enum_template.ftl")
        );

        // Генерация
        StringWriter writer = new StringWriter();
        template.process(data, writer);

        // Запись файла
        String fileName = className + targetLanguage.getFileExtension();
        FileUtils.writeToFile(outputDir, fileName, writer.toString());
    }

    private Map<String, Object> createTemplateData(
            String className, List<Object> enumValues, boolean isNumeric, String packagePath) {

        Map<String, Object> data = new HashMap<>();
        data.put("basePackage", basePackage);
        data.put("packagePath", packagePath);
        data.put("className", dehyphenizeClassName(capitalizeFirstLetter(className)));
        data.put("firstValueType", isNumeric ? "Integer" : "String");
        data.put("enumValues", prepareEnumConstants(enumValues));
        data.put("isNumeric", isNumeric);
        data.put("language", targetLanguage.getLanguageName());
        data.put("generatedAnnotation", GeneratorHelper.getGeneratedAnnotation(targetLanguage));
        data.put("generatedFileHeaderComment", GeneratorHelper.generatedFileHeaderComment);
        data.put("importGeneratedAnnotation", GeneratorHelper.importGeneratedAnnotation);

        return data;
    }

    private List<Map<String, Object>> prepareEnumConstants(List<Object> values) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object value : values) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("constantName", enumName(String.valueOf(value)));
            entry.put("value", value);
            result.add(entry);
        }
        return result;
    }

    private boolean isNumeric(Object value) {
        return String.valueOf(value).matches("\\d+");
    }

    private String enumName(String value) {
        if (value.matches("\\d+")) {
            return "NUMBER_" + value;
        }

        String snakeCase = value.replaceAll("([a-z])([A-Z]+)", "$1_$2");
        return snakeCase
                .toUpperCase()
                .replaceAll("[\\W_]", "_");
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
