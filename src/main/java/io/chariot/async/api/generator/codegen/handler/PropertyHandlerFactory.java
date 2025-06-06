package io.chariot.async.api.generator.codegen.handler;

import io.chariot.async.api.generator.codegen.DtoGenerator;
import io.chariot.async.api.generator.codegen.EnumGenerator;

import java.util.Map;

public class PropertyHandlerFactory {
    public static PropertyHandler createHandler(DtoGenerator dtoGenerator, EnumGenerator enumGenerator, Map<String, Object> propMap) {
        if (propMap.containsKey("$ref")) {
            return new RefPropertyHandler();
        } else if (propMap.containsKey("enum")) {
            return new EnumPropertyHandler(enumGenerator);
        } else if ("array".equals(propMap.get("type"))) {
            return new ArrayPropertyHandler(dtoGenerator);
        } else if (propMap.containsKey("properties")) {
            return new ObjectPropertyHandler(dtoGenerator);
        } else {
            return new SimplePropertyHandler();
        }
    }
}
