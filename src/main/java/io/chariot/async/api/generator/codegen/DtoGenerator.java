package io.chariot.async.api.generator.codegen;

import io.chariot.async.api.generator.codegen.processor.CaseInsensitiveSchemaProcessor;
import io.chariot.async.api.generator.codegen.processor.PropertyProcessor;
import io.chariot.async.api.generator.codegen.processor.SchemaProcessor;
import io.chariot.async.api.generator.io.FileManager;
import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.render.TemplateRenderer;
import io.chariot.async.api.generator.shema.AsyncApiModel;
import io.chariot.async.api.generator.shema.components.Schema;
import io.chariot.async.api.generator.shema.components.SchemaProperties;
import io.chariot.async.api.generator.utils.GeneratorHelper;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.*;

public class DtoGenerator extends BaseGenerator {
    private final AsyncApiModel model;
    private final SchemaProcessor schemaProcessor;
    private final PropertyProcessor propertyProcessor;
    private final EnumGenerator enumGenerator;

    public DtoGenerator(String basePackage, TargetLanguage language, TemplateRenderer templateRenderer, FileManager fileManager, AsyncApiModel model, SchemaProcessor schemaProcessor, PropertyProcessor propertyProcessor, EnumGenerator enumGenerator) {
        super(basePackage, language, templateRenderer, fileManager);
        this.model = model;
        this.schemaProcessor = schemaProcessor;
        this.propertyProcessor = propertyProcessor;
        this.enumGenerator = enumGenerator;
    }

    public void generate(Map<String, Schema> schemas, String outputDir) {
        String packagePath = "dto";
        File dir = createOutputDirectory(outputDir, packagePath);

        // Process case-insensitive schema names
        if (schemaProcessor instanceof CaseInsensitiveSchemaProcessor) {
            Map<String, String> renamedClasses = ((CaseInsensitiveSchemaProcessor) schemaProcessor)
                    .renameCaseInsensitiveSchemas(schemas);
        }

        for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
            String className = schemaProcessor.processSchemaName(entry.getKey());
            Schema schema = entry.getValue();

            if (schema.isEnum()) {
                generateEnum(className, schema, dir, packagePath);
            } else {
                generateDto(className, schema, dir, packagePath);
            }
        }
    }

    private void generateEnum(String className, Schema schema, File dir, String packagePath) {
        try {
            enumGenerator.generate(className, schema.getEnumValues(), dir, packagePath);
        } catch (Exception e) {
            throw new RuntimeException("Error generating Enum: " + className, e);
        }
    }

    public void generateDto(String className, Schema schema, File dir, String packagePath) {
        try {
            if (schema.getAllOf() != null) {
                // Используем schemaProcessor для обработки allOf
                SchemaProperties combined = schemaProcessor.processAllOf(schema, model.getComponents().getSchemas());
                schema.setProperties(combined.getProperties());
                schema.setRequired(combined.getRequired());
            }

            Map<String, Object> templateData = prepareTemplateData(className, schema, dir, packagePath);

            String fileName = className + language.getFileExtension();
            renderTemplate("dto_template.ftl", templateData, dir, fileName);
        } catch (Exception e) {
            throw new RuntimeException("Error generating DTO: " + className, e);
        }
    }

    private Map<String, Object> prepareTemplateData(String className, Schema schema, File dir, String packagePath) {
        Map<String, Object> data = prepareCommonTemplateData(className, packagePath);

        List<Map<String, Object>> fields = new ArrayList<>();
        Set<String> imports = new LinkedHashSet<>();

        // Process schema properties
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
        data.put("hasBuilder", !fields.isEmpty() && language == TargetLanguage.JAVA);

        return data;
    }
}