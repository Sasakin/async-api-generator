package io.chariot.async.api.generator.codegen.handler;

import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.languages.imports.DtoImportHandler;
import io.chariot.async.api.generator.languages.imports.LanguageSupportFactory;
import io.chariot.async.api.generator.languages.mappers.TypeMapper;
import io.chariot.async.api.generator.shema.components.Schema;
import io.chariot.async.api.generator.utils.FieldUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

class SimplePropertyHandler implements PropertyHandler {

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
        String type = (String) propMap.get("type");
        String format = (String) propMap.get("format");

        boolean hasProperties = propMap.get("properties") != null;
        String fieldType = TypeMapper.mapType(language, type, format, "Object", hasProperties);

        boolean isRequired = schema.getRequired() != null &&
                schema.getRequired().contains(fieldName);

        var languageSupport = LanguageSupportFactory.getLanguageSupport(language);

        // Добавляем импорты для типа
        languageSupport.addImportsForType(imports, fieldType);

        // Добавляем импорты для аннотаций валидации
        languageSupport.addValidationImports(imports, propMap, isRequired);

        Map<String, Object> field = FieldUtils.createField(
                fieldName,
                fieldType,
                isRequired
        );

        // Генерируем аннотации валидации
        String validationAnnotations = languageSupport.generateValidationAnnotations(propMap, isRequired);

        if (!validationAnnotations.isEmpty()) {
            field.put("validationAnnotations", validationAnnotations);
        }

        fields.add(field);
    }
}
