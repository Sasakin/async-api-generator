package io.chariot.async.api.generator.codegen.handler;

import io.chariot.async.api.generator.codegen.DtoGenerator;
import io.chariot.async.api.generator.codegen.processor.DtoPropertyProcessor;
import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.languages.imports.DtoImportHandler;
import io.chariot.async.api.generator.languages.mappers.TypeMapper;
import io.chariot.async.api.generator.shema.components.Schema;
import io.chariot.async.api.generator.utils.FieldUtils;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
class ArrayPropertyHandler implements PropertyHandler {

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
        @SuppressWarnings("unchecked")
        Map<String, Object> items = (Map<String, Object>) propMap.get("items");

        String fieldType;
        if (items.containsKey("$ref")) {
            String ref = (String) items.get("$ref");
            String refClassName = FieldUtils.extractClassNameFromRef(ref);
            fieldType = TypeMapper.mapArrayType(language, refClassName, false);
            DtoImportHandler.addImportsForArrayType(imports, language, refClassName);
        } else {
            String itemType = (String) items.get("type");
            String itemFormat = (String) items.get("format");
            String proposedClassName = FieldUtils.buildInlineArrayItemClassName(fieldName);
            boolean hasProperties = items.get("properties") != null;

            String itemTypeStr = TypeMapper.mapType(
                    language,
                    itemType,
                    itemFormat,
                    proposedClassName,
                    hasProperties
            );

            fieldType = TypeMapper.mapArrayType(language, itemTypeStr, false);
            DtoImportHandler.addImportsForArrayType(imports, language, itemTypeStr);

            if (hasProperties && itemTypeStr.equals(proposedClassName)) {
                Schema nestedSchema = Schema.of(items);
                dtoGenerator.generateDto(
                        proposedClassName,
                        nestedSchema,
                        outputDir,
                        packagePath
                );
            }
        }

        Map<String, Object> field = FieldUtils.createField(
                fieldName,
                fieldType,
                schema.getRequired() != null && schema.getRequired().contains(fieldName)
        );
        fields.add(field);
    }
}
