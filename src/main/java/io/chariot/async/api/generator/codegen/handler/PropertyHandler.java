package io.chariot.async.api.generator.codegen.handler;

import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.shema.components.Schema;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PropertyHandler {
    void handle(
            String fieldName,
            Map<String, Object> propMap,
            Schema schema,
            List<Map<String, Object>> fields,
            Set<String> imports,
            File outputDir,
            String packagePath,
            TargetLanguage language,
            String basePackage
    );
}
