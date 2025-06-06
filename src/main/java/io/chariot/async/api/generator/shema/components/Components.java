package io.chariot.async.api.generator.shema.components;

import lombok.Data;

import java.util.Map;

@Data
public class Components {

    private Map<String, Object> messageTraits;

    private Map<String, Schema> schemas;

    private Map<String, Object> messages;

}
