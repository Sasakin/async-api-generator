package io.chariot.async.api.generator.shema.components;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SchemaProperties {
    public final Map<String, Object> properties;
    public final List<String> required;

    public SchemaProperties(Map<String, Object> properties, List<String> required) {
        this.properties = properties;
        this.required = required;
    }
}
