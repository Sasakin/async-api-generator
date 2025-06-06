package io.chariot.async.api.generator.shema.properties;

import io.chariot.async.api.generator.shema.components.Schema;

import java.util.List;

public class EnumProperty extends BaseProperty {
    public EnumProperty(Class<?> type) {
        super("enumValues", type);
    }

    @Override
    public Class<?> getType() {
        return List.class;
    }

    @Override
    public Object get(Object instance) {
        return ((Schema) instance).getEnumValues();
    }

    @Override
    public void set(Object instance, Object value) {
        ((Schema) instance).setEnumValues((List<Object>) value);
    }
}
