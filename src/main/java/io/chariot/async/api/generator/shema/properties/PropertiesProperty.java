package io.chariot.async.api.generator.shema.properties;

import io.chariot.async.api.generator.shema.components.Schema;

import java.util.Map;

public class PropertiesProperty extends BaseProperty {
    public PropertiesProperty(Class<?> type) {
        super("properties", type);
    }

    @Override
    public Class<?> getType() {
        return Map.class;
    }

    @Override
    public Object get(Object instance) {
        return ((Schema) instance).getProperties();
    }

    @Override
    public void set(Object instance, Object value) {
        ((Schema) instance).setProperties((Map<String, Object>) value);
    }
}
