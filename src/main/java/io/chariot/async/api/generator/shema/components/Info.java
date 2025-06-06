package io.chariot.async.api.generator.shema.components;

import lombok.Data;

import java.util.Map;

@Data
public class Info {
    private String title;
    private String version;
    private String description;
    private Map<String, Object> externalDocs;
}
