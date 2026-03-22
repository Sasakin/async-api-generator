package io.chariot.async.api.generator;

import javax.inject.Inject;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class AsyncApiExtension {
    private final Property<String> inputYaml;
    private final Property<String> outputDir;
    private final Property<String> basePackage;

    @Inject
    public AsyncApiExtension(ObjectFactory objectFactory) {
        this.inputYaml = objectFactory.property(String.class).convention("asyncapi.yml");
        this.outputDir = objectFactory.property(String.class).convention("generated-sources/main/java");
        this.basePackage = objectFactory.property(String.class).convention("com.example");
    }

    public Property<String> getInputYaml() { return inputYaml; }
    public Property<String> getOutputDir() { return outputDir; }
    public Property<String> getBasePackage() { return basePackage; }
}
