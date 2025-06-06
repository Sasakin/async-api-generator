package io.chariot.async.api.generator.languages.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Поддержка импортов и аннотаций для Kotlin
 */
public class KotlinImportSupport extends BaseLanguageSupport {

    @Override
    public void addImportsForType(Set<String> imports, String fieldType) {
        if ("UUID".equals(fieldType)) {
            imports.add("java.util.UUID");
            imports.add("kotlinx.serialization.Contextual");
        } else if ("OffsetDateTime".equals(fieldType)) {
            imports.add("java.time.OffsetDateTime");
            imports.add("kotlinx.serialization.Contextual");
        } else if ("LocalDateTime".equals(fieldType)) {
            imports.add("java.time.LocalDateTime");
            imports.add("kotlinx.serialization.Contextual");
        }
    }

    @Override
    public void addImportsForArrayType(Set<String> imports, String itemType) {
        imports.add("kotlin.collections.List");
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
            annotations.add("@field:NotNull");
        }

        if (propMap.containsKey("pattern")) {
            String pattern = (String) propMap.get("pattern");
            annotations.add("@field:Pattern(regexp = \"" + escapePattern(pattern) + "\")");
        }

        String type = (String) propMap.get("type");
        if ("integer".equals(type)) {
            addMinMaxAnnotations(annotations, propMap);
        } else if ("number".equals(type)) {
            addDecimalMinMaxAnnotations(annotations, propMap);
        }

        addSizeAnnotations(annotations, propMap);

        if (annotations.isEmpty()) {
            return "";
        }
        return String.join("\n    ", annotations);
    }

    private void addMinMaxAnnotations(List<String> annotations, Map<String, Object> propMap) {
        if (propMap.containsKey("minimum")) {
            Object min = propMap.get("minimum");
            if (min instanceof Number) {
                annotations.add("@field:Min(" + ((Number) min).longValue() + "L)");
            }
        }

        if (propMap.containsKey("maximum")) {
            Object max = propMap.get("maximum");
            if (max instanceof Number) {
                annotations.add("@field:Max(" + ((Number) max).longValue() + "L)");
            }
        }
    }

    private void addDecimalMinMaxAnnotations(List<String> annotations, Map<String, Object> propMap) {
        if (propMap.containsKey("minimum")) {
            Object min = propMap.get("minimum");
            if (min != null) {
                annotations.add("@field:DecimalMin(\"" + min.toString() + "\")");
            }
        }

        if (propMap.containsKey("maximum")) {
            Object max = propMap.get("maximum");
            if (max != null) {
                annotations.add("@field:DecimalMax(\"" + max.toString() + "\")");
            }
        }
    }

    private void addSizeAnnotations(List<String> annotations, Map<String, Object> propMap) {
        boolean hasSizeConstraint = false;
        StringBuilder sizeAnnotation = new StringBuilder("@field:Size(");

        if (propMap.containsKey("minLength") || propMap.containsKey("minItems")) {
            Object minValue = propMap.get("minLength") != null ?
                    propMap.get("minLength") : propMap.get("minItems");
            if (minValue != null) {
                sizeAnnotation.append("min = ").append(minValue);
                hasSizeConstraint = true;
            }
        }

        if (propMap.containsKey("maxLength") || propMap.containsKey("maxItems")) {
            Object maxValue = propMap.get("maxLength") != null ?
                    propMap.get("maxLength") : propMap.get("maxItems");
            if (maxValue != null) {
                if (hasSizeConstraint) {
                    sizeAnnotation.append(", ");
                }
                sizeAnnotation.append("max = ").append(maxValue);
                hasSizeConstraint = true;
            }
        }

        if (hasSizeConstraint) {
            sizeAnnotation.append(")");
            annotations.add(sizeAnnotation.toString());
        }
    }

    @Override
    protected void addMinMaxImport(Set<String> imports) {
        imports.add("javax.validation.constraints.Min");
        imports.add("javax.validation.constraints.Max");
    }

    @Override
    protected void addDecimalMinMaxImport(Set<String> imports) {
        imports.add("javax.validation.constraints.DecimalMin");
        imports.add("javax.validation.constraints.DecimalMax");
    }

    @Override
    protected void addNotNullImport(Set<String> imports) {
        imports.add("javax.validation.constraints.NotNull");
    }

    @Override
    protected void addPatternImport(Set<String> imports) {
        imports.add("javax.validation.constraints.Pattern");
    }

    @Override
    protected void addSizeImport(Set<String> imports) {
        imports.add("javax.validation.constraints.Size");
    }

    @Override
    protected String getSizeAnnotationStart() {
        return "@field:Size(";
    }
}