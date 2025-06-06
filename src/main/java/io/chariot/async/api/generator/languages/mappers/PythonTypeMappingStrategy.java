package io.chariot.async.api.generator.languages.mappers;

import java.util.Map;
import java.util.Objects;

// Python стратегия
class PythonTypeMappingStrategy implements TypeMapper.TypeMappingStrategy {
    @Override
    public String mapType(String yamlType, String format, String className, boolean hasProperties) {
        if (yamlType == null) return "Any";

        switch (yamlType) {
            case "string":
                return pythonStringTypeByFormat(format);
            case "integer", "number", "long":
                return  "number".equals(yamlType) ? "float" : "int";
            case "boolean":
                return "bool";
            case "array":
                return "List";
            case "object":
                return hasProperties ? className : "Dict";
            default:
                return yamlType;
        }
    }

    private String pythonStringTypeByFormat(String format) {
        if (Objects.isNull(format)) {
            return "str";
        }
        return switch (format) {
            case "uuid", "date-time" -> "str"; // В Python обычно строки для этих типов
            default -> "str";
        };
    }

    @Override
    public String mapArrayType(String itemType, boolean isNullable) {
        return "List[" + itemType + "]";
    }

    @Override
    public String mapObjectType(String className, boolean isNullable) {
        return "Dict";
    }
}
