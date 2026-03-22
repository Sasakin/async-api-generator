package io.chariot.async.api.generator.utils;

import java.util.HashMap;
import java.util.Map;

public class FieldUtils {
    public static Map<String, Object> createField(String name, String type, boolean required) {
        Map<String, Object> field = new HashMap<>();
        field.put("type", type);
        field.put("name", name);
        field.put("required", required);
        return field;
    }

    public static String extractClassNameFromRef(String ref) {
        return ref.replace("#/components/schemas/", "");
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String buildInlineClassName(String fieldName) {
        return capitalizeFirstLetter(fieldName) + "Data";
    }

    public static String buildInlineArrayItemClassName(String fieldName) {
        return capitalizeFirstLetter(fieldName) + "Item";
    }
}
