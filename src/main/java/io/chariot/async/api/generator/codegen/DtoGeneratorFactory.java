package io.chariot.async.api.generator.codegen;

import freemarker.template.Configuration;
import io.chariot.async.api.generator.codegen.processor.CaseInsensitiveSchemaProcessor;
import io.chariot.async.api.generator.codegen.processor.DtoPropertyProcessor;
import io.chariot.async.api.generator.codegen.processor.SchemaProcessor;

import io.chariot.async.api.generator.io.FileManager;
import io.chariot.async.api.generator.io.JavaFileManager;
import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.render.FreemarkerTemplateRenderer;
import io.chariot.async.api.generator.render.TemplateRenderer;
import io.chariot.async.api.generator.shema.AsyncApiModel;

public class DtoGeneratorFactory {

    public static DtoGenerator create(
            AsyncApiModel model,
            String basePackage,
            TargetLanguage language
    ) {
        // Create schema processor
        SchemaProcessor schemaProcessor = new CaseInsensitiveSchemaProcessor();

        // Create enum generator
        EnumGenerator enumGenerator = new EnumGenerator(basePackage, language);

        // Create template renderer
        Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        freemarkerConfig.setClassForTemplateLoading(DtoGenerator.class, "/templates");
        TemplateRenderer templateRenderer = new FreemarkerTemplateRenderer(freemarkerConfig);

        // Create file manager
        FileManager fileManager = new JavaFileManager();

        // Создаем property processor с ссылкой на DtoGenerator
        DtoPropertyProcessor propertyProcessor = new DtoPropertyProcessor(enumGenerator);

        // Обновляем DtoGenerator с правильным PropertyProcessor
        DtoGenerator dtoGenerator = new DtoGenerator(
                basePackage,
                language,
                templateRenderer,
                fileManager,
                model,
                schemaProcessor,
                propertyProcessor,
                enumGenerator
        );

        propertyProcessor.setDtoGenerator(dtoGenerator);

        return dtoGenerator;
    }
}