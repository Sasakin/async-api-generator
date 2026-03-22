package io.chariot.async.api.generator.shema.components;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Headers {
    private String type;
    private List<String> required;
    private Map<String, Property> properties;
    private boolean additionalProperties;

}
