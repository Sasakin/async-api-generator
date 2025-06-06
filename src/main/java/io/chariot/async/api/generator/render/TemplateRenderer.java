package io.chariot.async.api.generator.render;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import io.chariot.async.api.generator.languages.TargetLanguage;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface TemplateRenderer {
    void renderDtoTemplate(
            String templateName,
            Map<String, Object> data,
            File outputDir,
            String fileName,
            TargetLanguage language
    ) throws IOException, TemplateException;

    Configuration getConfiguration();
}
