package io.chariot.async.api.generator.render;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.chariot.async.api.generator.languages.TargetLanguage;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RequiredArgsConstructor
public class FreemarkerTemplateRenderer implements TemplateRenderer {

    private final Configuration configuration;

    @Override
    public void renderDtoTemplate(
            String templateName,
            Map<String, Object> data,
            File outputDir,
            String fileName,
            TargetLanguage language
    ) throws IOException, TemplateException {
        String templatePath = language.getTemplatePath(templateName);
        Template template = configuration.getTemplate(templatePath);

        StringWriter writer = new StringWriter();
        template.process(data, writer);

        String content = writer.toString();

        Path filePath = outputDir.toPath().resolve(fileName);
        Files.writeString(filePath, content);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
