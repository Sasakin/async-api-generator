package io.chariot.async.api.generator.languages.mappers;

import java.util.Objects;
import java.util.Optional;

/**
 * Маппинг типов для Java
 */
class JavaTypeMappingStrategy implements TypeMapper.TypeMappingStrategy {

    @Override
    public String mapType(String yamlType, String format, String className, boolean hasProperties) {
        if (yamlType == null) return "Object";

        return switch (yamlType) {
            case "string" -> mapStringType(format);
            case "integer" -> mapIntegerType(format);
            case "number" -> mapNumberType(format);
            case "boolean" -> "Boolean";
            case "array" -> "List<?>";
            case "object" -> hasProperties ? className : Optional.ofNullable(className).orElse("Object");
            default -> yamlType;
        };
    }

    private String mapStringType(String format) {
        if (Objects.isNull(format)) {
            return "String";
        }
        return switch (format) {
            case "uuid" -> "UUID";
            case "date-time" -> "Instant";
            case "date" -> "LocalDate";
            default -> "String";
        };
    }

    private String mapIntegerType(String format) {
        if (Objects.isNull(format)) {
            return "Integer";
        }
        return switch (format) {
            case "int32" -> "Integer";
            case "int64" -> "Long";
            default -> "Integer";
        };
    }

    private String mapNumberType(String format) {
        if (Objects.isNull(format)) {
            return "Double";
        }
        return switch (format) {
            case "float" -> "Float";
            case "double" -> "Double";
            case "decimal", "bigdecimal" -> "BigDecimal";
            default -> "Double";
        };
    }

    @Override
    public String mapArrayType(String itemType, boolean isNullable) {
        return "List<" + itemType + ">";
    }

    @Override
    public String mapObjectType(String className, boolean isNullable) {
        return className;
    }
}