package io.chariot.async.api.generator.parser;

import io.chariot.async.api.generator.shema.AsyncApiModel;
import io.chariot.async.api.generator.utils.CustomPropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class YamlParser {
    private static final Logger log = LoggerFactory.getLogger(YamlParser.class);

    public static AsyncApiModel parse(String filePath) {
        Constructor constructor = new Constructor(AsyncApiModel.class, new LoaderOptions());
        constructor.setPropertyUtils(new CustomPropertyUtils());
        Yaml yaml = new Yaml(constructor);

        AsyncApiModel result = parseYamlFromResource(yaml, filePath);
        if(result != null) return result;
        result = parseYamlFromDiskFile(yaml, filePath);
        if(result != null) return result;

        log.error("All attempts failed to parse YAML from '{}'", filePath);
        throw new RuntimeException("AsyncAPI model generation failed.");
    }

    private static AsyncApiModel parseYamlFromResource(Yaml yaml, String filePath) {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        try (in) {
            log.info("Start loading resource '{}'", filePath);
            return yaml.loadAs(in, AsyncApiModel.class);
        } catch (Throwable e) {
            log.info("Could not parse YAML resource '{}'. Trying to switch to another resource, if specified.", filePath);
            return null;
        }
    }

    private static AsyncApiModel parseYamlFromDiskFile(Yaml yaml, String filePath) {
        try {
            log.info("Start loading file '{}'", filePath);
            return yaml.loadAs(Files.readString(Path.of(filePath)), AsyncApiModel.class);
        } catch (IOException e) {
            log.info("Could not read file '{}}'. Trying to switch to another resource, if specified.", filePath);
            return null;
        }
    }
}
