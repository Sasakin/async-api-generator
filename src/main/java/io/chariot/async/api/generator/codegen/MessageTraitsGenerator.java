package io.chariot.async.api.generator.codegen;

import freemarker.template.Configuration;
import io.chariot.async.api.generator.codegen.processor.CaseInsensitiveSchemaProcessor;
import io.chariot.async.api.generator.codegen.processor.DtoPropertyProcessor;
import io.chariot.async.api.generator.codegen.processor.PropertyProcessor;
import io.chariot.async.api.generator.codegen.processor.SchemaProcessor;
import io.chariot.async.api.generator.io.FileManager;
import io.chariot.async.api.generator.io.JavaFileManager;
import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.render.FreemarkerTemplateRenderer;
import io.chariot.async.api.generator.render.TemplateRenderer;
import io.chariot.async.api.generator.shema.components.Schema;
import io.chariot.async.api.generator.utils.GeneratorHelper;

import java.io.File;
import java.util.*;

public class MessageTraitsGenerator extends BaseGenerator {
    private final PropertyProcessor propertyProcessor;

    public MessageTraitsGenerator(String basePackage, TargetLanguage language, TemplateRenderer templateRenderer, FileManager fileManager, PropertyProcessor propertyProcessor) {
        super(basePackage, language, templateRenderer, fileManager);
        this.propertyProcessor = propertyProcessor;
    }

    public void generate(Map<String, Object> traits, String outputDir) {
        String packagePath = "messagetraits";
        if (traits == null || traits.isEmpty()) {
            System.out.println("No traits to generate, skipping.");
            return;
        }

        File dir = createOutputDirectory(outputDir, packagePath);

        for (Map.Entry<String, Object> traitEntry : traits.entrySet()) {
            String traitName = GeneratorHelper.dehyphenizeClassName(capitalizeFirstLetter(traitEntry.getKey()));
            Object trait = traitEntry.getValue();

            if (trait instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> traitMap = (Map<String, Object>) trait;

                if (traitMap.containsKey("headers")) {
                    Object headers = traitMap.get("headers");
                    if (headers instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> headersMap = (Map<String, Object>) headers;
                        generateTraitClass(traitName, headersMap, dir, packagePath);
                    }
                }
            }
        }
    }

    private void generateTraitClass(String className, Map<String, Object> headers, File dir, String packagePath) {
        try {
            // Преобразуем headers в Schema
            Schema schema = convertToSchema(headers);

            Map<String, Object> templateData = prepareTemplateData(className, schema, dir, packagePath);

            String fileName = className + language.getFileExtension();
            renderTemplate("messagetrait_template.ftl", templateData, dir, fileName);
        } catch (Exception e) {
            throw new RuntimeException("Error generating Trait: " + className, e);
        }
    }

    private Schema convertToSchema(Map<String, Object> headers) {
        Schema schema = new Schema();
        schema.setType("object");
        schema.setProperties((Map<String, Object>) headers.get("properties"));
        schema.setRequired((List<String>) headers.get("required"));
        return schema;
    }

    private Map<String, Object> prepareTemplateData(String className, Schema schema, File dir, String packagePath) {
        Map<String, Object> data = prepareCommonTemplateData(className, packagePath);

        List<Map<String, Object>> fields = new ArrayList<>();
        Set<String> imports = new LinkedHashSet<>();

        // Используем существующий PropertyProcessor для обработки свойств
        propertyProcessor.processProperties(
                schema,
                fields,
                imports,
                dir,
                packagePath,
                language,
                basePackage
        );

        data.put("fields", fields);
        data.put("imports", new ArrayList<>(imports));
        data.put("hasBuilder", false);

        return data;
    }
}
