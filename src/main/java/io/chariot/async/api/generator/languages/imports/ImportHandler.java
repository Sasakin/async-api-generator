package io.chariot.async.api.generator.languages.imports;

import io.chariot.async.api.generator.languages.TargetLanguage;
import java.util.Map;
import java.util.Set;

/**
 * Интерфейс для обработки импортов в зависимости от языка
 */
public interface ImportHandler {
    void addImportsForType(Set<String> imports, String fieldType);
    void addImportsForArrayType(Set<String> imports, String itemType);
    void addValidationImports(Set<String> imports, Map<String, Object> propMap, boolean isRequired);
}
