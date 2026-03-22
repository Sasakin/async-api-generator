package io.chariot.async.api.generator.codegen.processor;

import io.chariot.async.api.generator.shema.components.Schema;
import io.chariot.async.api.generator.shema.components.SchemaProperties;

import java.util.Map;

public interface SchemaProcessor {
    String processSchemaName(String originalName);
    SchemaProperties processAllOf(Schema schema, Map<String, Schema> allSchemas);
    Schema resolveRef(Schema schema, Map<String, Schema> allSchemas);
}
