package io.chariot.async.api.generator.shema.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Ref {
    @JsonProperty("$ref")
    private String ref;
}
