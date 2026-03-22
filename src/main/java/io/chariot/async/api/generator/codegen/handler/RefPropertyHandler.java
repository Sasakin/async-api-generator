package io.chariot.async.api.generator.codegen.handler;

import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.shema.components.Schema;
import io.chariot.async.api.generator.utils.FieldUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

class RefPropertyHandler implements PropertyHandler {

    public RefPropertyHandler() {
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
        String ref = (String) propMap.get("$ref");
        String refClassName = FieldUtils.extractClassNameFromRef(ref);

        Map<String, Object> field = FieldUtils.createField(
                fieldName,
                refClassName,
                schema.getRequired() != null && schema.getRequired().contains(fieldName)
        );
        fields.add(field);
    }
}
