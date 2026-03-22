package io.chariot.async.api.generator.languages.mappers;

import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.languages.imports.LanguageSupportFactory;

import java.math.BigDecimal;
import java.util.Map;

public class TypeMapper {
    public static String mapType(TargetLanguage language, String yamlType, String format,
                                 String className, boolean hasProperties) {
        TypeMappingStrategy strategy = TypeMapperFactory.getStrategy(language);
        return strategy.mapType(yamlType, format, className, hasProperties);
    }

    public static String mapArrayType(TargetLanguage language, String itemType, boolean isNullable) {
        TypeMappingStrategy strategy = TypeMapperFactory.getStrategy(language);
        return strategy.mapArrayType(itemType, isNullable);
    }

    public static String mapObjectType(TargetLanguage language, String className, boolean isNullable) {
        TypeMappingStrategy strategy = TypeMapperFactory.getStrategy(language);
        return strategy.mapObjectType(className, isNullable);
    }

    public static String generateValidationAnnotations(
            TargetLanguage language,
            Map<String, Object> propMap,
            boolean isRequired
    ) {
        return LanguageSupportFactory.getValidationGenerator(language)
                .generateValidationAnnotations(propMap, isRequired);
    }

    public static boolean isNumericType(String yamlType) {
        return "integer".equals(yamlType) || "number".equals(yamlType);
    }

    public static boolean isIntegerType(String yamlType) {
        return "integer".equals(yamlType);
    }

    public static boolean isFloatingPointType(String yamlType) {
        return "number".equals(yamlType);
    }

    public static BigDecimal convertToBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        return new BigDecimal(value.toString());
    }

    interface TypeMappingStrategy {
        String mapType(String yamlType, String format, String className, boolean hasProperties);
        String mapArrayType(String itemType, boolean isNullable);
        String mapObjectType(String className, boolean isNullable);
    }

}