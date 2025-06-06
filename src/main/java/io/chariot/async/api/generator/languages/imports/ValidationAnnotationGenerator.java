package io.chariot.async.api.generator.languages.imports;

import java.util.Map;

/**
 * Интерфейс для генерации аннотаций валидации
 */
public interface ValidationAnnotationGenerator {
    String generateValidationAnnotations(Map<String, Object> propMap, boolean isRequired);
}