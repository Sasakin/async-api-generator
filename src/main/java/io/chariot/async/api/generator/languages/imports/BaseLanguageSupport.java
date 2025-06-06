package io.chariot.async.api.generator.languages.imports;

import java.util.Map;
import java.util.Set;

/**
 * Базовый класс для языковой поддержки
 */
public abstract class BaseLanguageSupport implements ImportHandler, ValidationAnnotationGenerator {

    protected String escapePattern(String pattern) {
        return pattern.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    protected void addNumericConstraintImports(Set<String> imports, Map<String, Object> propMap) {
        String type = (String) propMap.get("type");

        if ("integer".equals(type)) {
            addMinMaxImport(imports);
        } else if ("number".equals(type)) {
            addDecimalMinMaxImport(imports);
        } else {
            addMinMaxImport(imports);
            addDecimalMinMaxImport(imports);
        }
    }

    protected abstract void addMinMaxImport(Set<String> imports);
    protected abstract void addDecimalMinMaxImport(Set<String> imports);
    protected abstract void addNotNullImport(Set<String> imports);
    protected abstract void addPatternImport(Set<String> imports);
    protected abstract void addSizeImport(Set<String> imports);

    protected void addSizeAnnotations(StringBuilder annotations, Map<String, Object> propMap) {
        boolean hasMinLength = propMap.containsKey("minLength");
        boolean hasMaxLength = propMap.containsKey("maxLength");

        if (hasMinLength || hasMaxLength) {
            annotations.append(getSizeAnnotationStart());

            if (hasMinLength) {
                Object minLength = propMap.get("minLength");
                if (minLength instanceof Number) {
                    annotations.append("min = ").append(((Number) minLength).intValue());
                }
            }

            if (hasMinLength && hasMaxLength) {
                annotations.append(", ");
            }

            if (hasMaxLength) {
                Object maxLength = propMap.get("maxLength");
                if (maxLength instanceof Number) {
                    annotations.append("max = ").append(((Number) maxLength).intValue());
                }
            }

            annotations.append(")");
        }
    }

    protected abstract String getSizeAnnotationStart();
}
