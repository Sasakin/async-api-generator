package io.chariot.async.api.generator.utils;

import io.chariot.async.api.generator.shema.properties.*;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class CustomPropertyUtils extends PropertyUtils {

    @Override
    public Property getProperty(Class<?> type, String name, BeanAccess bAccess) {
        try {
            return bAccess != null ? super.getProperty(type, name, bAccess) : this.getProperty(type, name);
        } catch (YAMLException e) {
            System.err.println("Skipping unknown property: " + name);
            return new IgnoredProperty(); // Пропускаем неизвестные поля
        }
    }

    @Override
    public Property getProperty(Class<?> type, String name) {
        try {
            switch (name) {
                case "allOf":
                    return new AllOfProperty(type);
                case "$ref":
                    return super.getProperty(type, "ref");
                case "enum":
                    return new EnumProperty(type);
                case "default":
                    return new DefaultProperty(type);
                case "items":
                    return new ItemsProperty(type);
                case "properties":
                    return new PropertiesProperty(type);
                default:
                    return super.getProperty(type, name);
            }
        } catch (YAMLException e) {
            System.err.println("Skipping unknown property: " + name);
            return new IgnoredProperty(); // Пропускаем неизвестные поля
        }
    }
}
