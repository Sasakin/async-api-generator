package io.chariot.async.api.generator.shema.properties;

import org.yaml.snakeyaml.introspector.Property;

import java.lang.annotation.Annotation;
import java.util.List;

public class IgnoredProperty extends Property {
    public IgnoredProperty() {
        super("dummy", Object.class);
    }

    @Override public String getName() { return "dummy"; }
    @Override public Class<?> getType() { return Object.class; }

    @Override
    public Class<?>[] getActualTypeArguments() {
        return new Class[0];
    }

    @Override public Object get(Object bean) { return null; }

    @Override
    public List<Annotation> getAnnotations() {
        return List.of();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> aClass) {
        return null;
    }

    @Override public void set(Object bean, Object value) {} // Ничего не делаем
    @Override public boolean isReadable() { return false; }
}
