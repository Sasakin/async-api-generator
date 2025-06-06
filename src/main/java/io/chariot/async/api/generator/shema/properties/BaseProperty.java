package io.chariot.async.api.generator.shema.properties;

import org.yaml.snakeyaml.introspector.Property;

import java.lang.annotation.Annotation;
import java.util.List;

public abstract class BaseProperty extends Property {
    protected BaseProperty(String name, Class<?> type) {
        super(name, type);
    }

    @Override
    public Class<?>[] getActualTypeArguments() {
        return new Class[0];
    }

    @Override
    public List<Annotation> getAnnotations() {
        return List.of();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return null;
    }

    @Override
    public boolean isReadable() {
        return true;
    }
}
