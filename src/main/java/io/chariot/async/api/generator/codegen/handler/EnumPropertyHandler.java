package io.chariot.async.api.generator.codegen.handler;

import io.chariot.async.api.generator.codegen.EnumGenerator;
import io.chariot.async.api.generator.codegen.processor.DtoPropertyProcessor;
import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.shema.components.Schema;
import io.chariot.async.api.generator.utils.FieldUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

class EnumPropertyHandler implements PropertyHandler {
    private final EnumGenerator enumGenerator;

    public EnumPropertyHandler(EnumGenerator enumGenerator) {
        this.enumGenerator = enumGenerator;
    }

    @Override
    public void handle(
            String fieldName,
            Map<String, Object> propMap,
            Schema schema,
            List<Map<String, Object>> fields,
            Set<String> imports,
            File outputDir,
            String packagePath,
            TargetLanguage language,
            String basePackage
    ) {
        @SuppressWarnings("unchecked")
        List<Object> enumValues = (List<Object>) propMap.get("enum");

        if (enumValues != null && !enumValues.isEmpty()) {
            String enumClassName = capitalizeFirstLetter(fieldName) + "Enum";
            try {
                enumGenerator.generate(enumClassName, enumValues, outputDir, packagePath);
            } catch (Exception e) {
                throw new RuntimeException("Error generating Enum: " + enumClassName, e);
            }

            Map<String, Object> field = FieldUtils.createField(
                    fieldName,
                    enumClassName,
                    schema.getRequired() != null && schema.getRequired().contains(fieldName)
            );
            fields.add(field);
        }
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

}
