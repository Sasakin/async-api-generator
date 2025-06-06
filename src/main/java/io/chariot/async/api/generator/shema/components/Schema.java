package io.chariot.async.api.generator.shema.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Представляет JSON components.Schema из AsyncAPI components/schemas
 */
@Data
public class Schema {
    private String title;
    // Тип схемы (object, string, integer, array и т.д.)
    private String type;

    // Формат (uuid, base64 и т.д.)
    private String format;

    // Описание
    private String description;

    // Может ли быть null
    private Boolean nullable;

    private Object defaultValue;

    // Обязательные поля
    private List<String> required;
    private Map<String, Object> properties;

    // Элементы для массивов
    private Schema items;

    @JsonProperty("$ref")
    private String ref;

    // Enum значения
    private List<Object> enumValues;

    // Для числовых типов
    private Integer minimum;
    private Integer maximum;

    // Для строковых типов
    private String pattern;
    private Integer minLength;
    private Integer maxLength;

    // Дополнительные свойства
    private Boolean additionalProperties;

    private List<Schema> allOf;

    public static Schema of(Map<String, Object> map) {
        var schema =  new Schema();

        schema.additionalProperties = (Boolean) map.get("additionalProperties");
        schema.allOf = (List<Schema>) map.get("allOf");
        schema.properties = (Map<String, Object>) map.get("properties");
        schema.required = (List<String>) map.get("required");
        schema.type = (String) map.get("type");
        schema.description = (String) map.get("description");

        return schema;
    }

    public boolean isEnum() {
        return enumValues != null && !enumValues.isEmpty();
    }

    @Data
    public static class Property {
        // Тип свойства
        private String type;

        // Формат
        private String format;

        // Описание
        private String description;

        // Может ли быть null
        private Boolean nullable;

        // Ссылка на другую схему
        @JsonProperty("$ref")
        private String ref;

        // Для строковых полей
        private String pattern;
        private Integer maxLength;

        // Для числовых полей
        private Integer minimum;
        private Integer maximum;

        private Schema items;
        // Enum значения
        private List<String> enumValues;
    }
}

