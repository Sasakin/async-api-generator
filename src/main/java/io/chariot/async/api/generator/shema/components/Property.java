package io.chariot.async.api.generator.shema.components;

import lombok.Data;

@Data
public class Property {
    private String title;
    private String description;
    private String type;
    private String format;
    private boolean nullable;
}
