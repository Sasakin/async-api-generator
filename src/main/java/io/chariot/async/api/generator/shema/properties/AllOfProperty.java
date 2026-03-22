package io.chariot.async.api.generator.shema.properties;

import io.chariot.async.api.generator.shema.components.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllOfProperty extends BaseProperty {
    public AllOfProperty(Class<?> type) {
        super("allOf", type);
    }

    @Override
    public Class<?> getType() {
        return List.class; // allOf является списком схем
    }

    @Override
    public Object get(Object instance) {
        return ((Schema) instance).getAllOf();
    }

    @Override
    public void set(Object instance, Object value) {
        List<Schema> schemas = new ArrayList<>();
        if (value instanceof List) {
            for (Object item : (List<?>) value) {
                if (item instanceof Map) {
                    Schema schema = new Schema(); // Создаем новый объект Schema
                    // Копируем свойства из Map в Schema
                    ((Map<?, ?>) item).forEach((k, v) -> {
                        if (k.equals("properties")) {
                            schema.setProperties((Map<String, Object>) v);
                        } else if (k.equals("required")) {
                            schema.setRequired((List<String>) v);
                        } else if (k.equals("$ref")) {
                            schema.setRef((String) v);
                        }
                        // Добавьте обработку других полей, если нужно
                    });
                    schemas.add(schema);
                }
            }
        }
        ((Schema) instance).setAllOf(schemas);
    }
}
