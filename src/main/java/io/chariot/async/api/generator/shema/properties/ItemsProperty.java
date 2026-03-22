package io.chariot.async.api.generator.shema.properties;

import io.chariot.async.api.generator.shema.components.Schema;

public class ItemsProperty extends BaseProperty {
    public ItemsProperty(Class<?> type) {
        super("items", type);
    }

    @Override
    public Class<?> getType() {
        return Schema.class;
    }

    @Override
    public Object get(Object instance) {
        return ((Schema) instance).getItems();
    }

    @Override
    public void set(Object instance, Object value) {
        ((Schema) instance).setItems((Schema) value);
    }
}

