package io.chariot.async.api.generator.shema.components;

import lombok.Data;

import java.util.List;

@Data
public class Server {
    private String title;
    private String summary;
    private String host;
    private String protocol;
    private String protocolVersion;
    private String description;
    private List<Tag> tags;
}
