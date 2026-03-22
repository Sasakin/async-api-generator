package io.chariot.async.api.generator.languages.imports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Поддержка импортов и аннотаций для Java
 */
public class JavaImportSupport extends BaseLanguageSupport {

    @Override
    public void addImportsForType(Set<String> imports, String fieldType) {
        if ("UUID".equals(fieldType)) {
            imports.add("java.util.UUID");
        } else if ("Instant".equals(fieldType)) {
            imports.add("java.time.Instant");
        } else if ("LocalDate".equals(fieldType)) {
            imports.add("java.time.LocalDate");
        } else if ("BigDecimal".equals(fieldType)) {
            imports.add("java.math.BigDecimal");
        }
    }

    @Override
    public void addImportsForArrayType(Set<String> imports, String itemType) {
        imports.add("java.util.List");
        addImportsForType(imports, itemType);
    }

    @Override
    public void addValidationImports(Set<String> imports, Map<String, Object> propMap, boolean isRequired) {
        if (isRequired) {
            addNotNullImport(imports);
        }

        if (propMap.containsKey("pattern")) {
            addPatternImport(imports);
        }

        if (propMap.containsKey("minimum") || propMap.containsKey("maximum") ||
                propMap.containsKey("exclusiveMinimum") || propMap.containsKey("exclusiveMaximum")) {
            addNumericConstraintImports(imports, propMap);
        }

        if (propMap.containsKey("minLength") || propMap.containsKey("maxLength") ||
                propMap.containsKey("minItems") || propMap.containsKey("maxItems")) {
            addSizeImport(imports);
        }
    }

    @Override
    public String generateValidationAnnotations(Map<String, Object> propMap, boolean isRequired) {
        List<String> annotations = new ArrayList<>();

        if (isRequired) {
            annotations.add("@NotNull");
        }

        if (propMap.containsKey("pattern")) {
            String pattern = (String) propMap.get("pattern");
            annotations.add("@Pattern(regexp = \"" + escapePattern(pattern) + "\")");
        }

        String type = (String) propMap.get("type");
        if ("integer".equals(type)) {
            addMinMaxAnnotations(annotations, propMap);
        } else if ("number".equals(type)) {
            addDecimalMinMaxAnnotations(annotations, propMap);
        }

        addSizeAnnotations(annotations, propMap);

        // Собираем аннотации в одну строку с правильными переносами
        return String.join("\n    ", annotations);
    }

    private void addMinMaxAnnotations(List<String> annotations, Map<String, Object> propMap) {
        if (propMap.containsKey("minimum")) {
            Object min = propMap.get("minimum");
            if (min instanceof Number) {
                annotations.add("@Min(" + ((Number) min).longValue() + "L)");
            }
        }

        if (propMap.containsKey("maximum")) {
            Object max = propMap.get("maximum");
            if (max instanceof Number) {
                annotations.add("@Max(" + ((Number) max).longValue() + "L)");
            }
        }
    }

    private void addDecimalMinMaxAnnotations(List<String> annotations, Map<String, Object> propMap) {
        if (propMap.containsKey("minimum")) {
            Object min = propMap.get("minimum");
            if (min != null) {
                annotations.add("@DecimalMin(\"" + min.toString() + "\")");
            }
        }

        if (propMap.containsKey("maximum")) {
            Object max = propMap.get("maximum");
            if (max != null) {
                annotations.add("@DecimalMax(\"" + max.toString() + "\")");
            }
        }

        if (Boolean.TRUE.equals(propMap.get("exclusiveMinimum")) && propMap.containsKey("minimum")) {
            Object min = propMap.get("minimum");
            if (min != null) {
                BigDecimal minValue = new BigDecimal(min.toString());
                BigDecimal exclusiveValue = minValue.add(BigDecimal.valueOf(0.000001));
                annotations.add("@DecimalMin(value = \"" + exclusiveValue + "\", inclusive = false)");
            }
        }

        if (Boolean.TRUE.equals(propMap.get("exclusiveMaximum")) && propMap.containsKey("maximum")) {
            Object max = propMap.get("maximum");
            if (max != null) {
                BigDecimal maxValue = new BigDecimal(max.toString());
                BigDecimal exclusiveValue = maxValue.subtract(BigDecimal.valueOf(0.000001));
                annotations.add("@DecimalMax(value = \"" + exclusiveValue + "\", inclusive = false)");
            }
        }
    }

    private void addSizeAnnotations(List<String> annotations, Map<String, Object> propMap) {
        boolean hasMinLength = propMap.containsKey("minLength");
        boolean hasMaxLength = propMap.containsKey("maxLength");

        if (hasMinLength || hasMaxLength) {
            StringBuilder sizeAnnotation = new StringBuilder("@Size(");

            if (hasMinLength) {
                Object minLength = propMap.get("minLength");
                if (minLength instanceof Number) {
                    sizeAnnotation.append("min = ").append(((Number) minLength).intValue());
                }
            }

            if (hasMinLength && hasMaxLength) {
                sizeAnnotation.append(", ");
            }

            if (hasMaxLength) {
                Object maxLength = propMap.get("maxLength");
                if (maxLength instanceof Number) {
                    sizeAnnotation.append("max = ").append(((Number) maxLength).intValue());
                }
            }

            sizeAnnotation.append(")");
            annotations.add(sizeAnnotation.toString());
        }
    }

    @Override
    protected void addMinMaxImport(Set<String> imports) {
        imports.add("jakarta.validation.constraints.Min");
        imports.add("jakarta.validation.constraints.Max");
    }

    @Override
    protected void addDecimalMinMaxImport(Set<String> imports) {
        imports.add("jakarta.validation.constraints.DecimalMin");
        imports.add("jakarta.validation.constraints.DecimalMax");
    }

    @Override
    protected void addNotNullImport(Set<String> imports) {
        imports.add("jakarta.validation.constraints.NotNull");
    }

    @Override
    protected void addPatternImport(Set<String> imports) {
        imports.add("jakarta.validation.constraints.Pattern");
    }

    @Override
    protected void addSizeImport(Set<String> imports) {
        imports.add("jakarta.validation.constraints.Size");
    }

    @Override
    protected String getSizeAnnotationStart() {
        return "@Size(";
    }
}

