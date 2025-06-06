package io.chariot.async.api.generator.codegen.handler;

import io.chariot.async.api.generator.codegen.DtoGenerator;
import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.shema.components.Schema;
import io.chariot.async.api.generator.utils.FieldUtils;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
class ObjectPropertyHandler implements PropertyHandler {

    private final DtoGenerator dtoGenerator;

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
        String nestedDtoClassName = FieldUtils.buildInlineClassName(fieldName);

        Schema nestedSchema = Schema.of(propMap);
        dtoGenerator.generateDto(
                nestedDtoClassName,
                nestedSchema,
                outputDir,
                packagePath
        );

        Map<String, Object> field = FieldUtils.createField(
                fieldName,
                nestedDtoClassName,
                schema.getRequired() != null && schema.getRequired().contains(fieldName)
        );
        fields.add(field);
    }

}
