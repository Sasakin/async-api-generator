package io.chariot.async.api.generator.codegen;

import freemarker.template.Configuration;
import io.chariot.async.api.generator.codegen.processor.DtoPropertyProcessor;
import io.chariot.async.api.generator.codegen.processor.PropertyProcessor;
import io.chariot.async.api.generator.io.FileManager;
import io.chariot.async.api.generator.io.JavaFileManager;
import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.render.FreemarkerTemplateRenderer;
import io.chariot.async.api.generator.render.TemplateRenderer;

public class MessageTraitsGeneratorFactory {

    public static MessageTraitsGenerator create(
            String basePackage,
            TargetLanguage language
    ) {
        // Создаем enum generator
        EnumGenerator enumGenerator = new EnumGenerator(basePackage, language);

        // Создаем property processor для message traits
        PropertyProcessor propertyProcessor = new DtoPropertyProcessor(enumGenerator);

        // Создаем template renderer
        Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        freemarkerConfig.setClassForTemplateLoading(MessageTraitsGenerator.class, "/templates");
        TemplateRenderer templateRenderer = new FreemarkerTemplateRenderer(freemarkerConfig);

        // Создаем file manager
        FileManager fileManager = new JavaFileManager();

        // Создаем MessageTraitsGenerator
        return new MessageTraitsGenerator(
                basePackage,
                language,
                templateRenderer,
                fileManager,
                propertyProcessor
        );
    }
}
