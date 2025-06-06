package io.chariot.async.api.generator.codegen.processor;

import io.chariot.async.api.generator.codegen.DtoGenerator;
import io.chariot.async.api.generator.codegen.EnumGenerator;
import io.chariot.async.api.generator.codegen.handler.PropertyHandler;
import io.chariot.async.api.generator.codegen.handler.PropertyHandlerFactory;
import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.shema.components.Schema;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class DtoPropertyProcessor implements PropertyProcessor {

    @Setter
    private DtoGenerator dtoGenerator;

    private final EnumGenerator enumGenerator;

    @Override
    public void processProperties(
            Schema schema,
            List<Map<String, Object>> fields,
            Set<String> imports,
            File outputDir,
            String packagePath,
            TargetLanguage language,
            String basePackage
    ) {
        if (schema.getProperties() == null) return;

        for (Map.Entry<String, Object> propEntry : schema.getProperties().entrySet()) {
            processProperty(
                    propEntry.getKey(),
                    propEntry.getValue(),
                    schema,
                    fields,
                    imports,
                    outputDir,
                    packagePath,
                    language,
                    basePackage
            );
        }
    }

    private void processProperty(
            String fieldName,
            Object rawValue,
            Schema schema,
            List<Map<String, Object>> fields,
            Set<String> imports,
            File outputDir,
            String packagePath,
            TargetLanguage language,
            String basePackage
    ) {
        if (!(rawValue instanceof Map)) return;

        @SuppressWarnings("unchecked")
        Map<String, Object> propMap = (Map<String, Object>) rawValue;

        PropertyHandler handler = PropertyHandlerFactory.createHandler(dtoGenerator, enumGenerator, propMap);
        handler.handle(
                fieldName,
                propMap,
                schema,
                fields,
                imports,
                outputDir,
                packagePath,
                language,
                basePackage
        );
    }

}
