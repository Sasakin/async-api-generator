package io.chariot.async.api.generator.shema.properties;

import io.chariot.async.api.generator.shema.components.Schema;

public class DefaultProperty extends BaseProperty {
    public DefaultProperty(Class<?> type) {
        super("defaultValue", type);
    }

    @Override
    public Class<?> getType() {
        return Object.class;
    }

    @Override
    public Object get(Object instance) {
        return ((Schema) instance).getDefaultValue();
    }

    @Override
    public void set(Object instance, Object value) {
        ((Schema) instance).setDefaultValue(value);
    }
}
