package io.chariot.async.api.generator.shema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.chariot.async.api.generator.shema.components.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsyncApiModel {
    private String id;
    private String asyncapi;
    private String defaultContentType;
    private Info info;
    private Map<String, Server> servers;
    private Components components;
    private Map<String, Channel> channels;
    private Map<String, Operation> operations;
    private Map<String, MessageTrait> messageTraits;

    @Data
    public static class Channel {
        private String address;
        private String title;
        private String summary;
        private String description;
        private List<Ref> servers;
        private Map<String, Ref> messages;
    }

}
