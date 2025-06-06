package io.chariot.async.api.generator.shema.components;

import lombok.Data;

@Data
public class Operation {
    private String action;
    private Ref channel;
    private String title;
    private String summary;
    private String description;
}
