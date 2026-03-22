package io.chariot.async.api.generator.codegen;

import freemarker.template.TemplateException;
import io.chariot.async.api.generator.io.FileManager;
import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.render.TemplateRenderer;
import io.chariot.async.api.generator.utils.GeneratorHelper;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public abstract class BaseGenerator {
    protected final String basePackage;
    protected final TargetLanguage language;
    protected final TemplateRenderer templateRenderer;
    protected final FileManager fileManager;

    protected File createOutputDirectory(String outputDir, String packagePath) {
        return fileManager.createDirectory(
                outputDir + File.separator + basePackage.replace(".", File.separator) +
                        File.separator + packagePath.replace(".", File.separator)
        );
    }

    protected void renderTemplate(
            String templateName,
            Map<String, Object> templateData,
            File outputDir,
            String fileName
    ) throws IOException, TemplateException {
        templateRenderer.renderDtoTemplate(
                templateName,
                templateData,
                outputDir,
                fileName,
                language
        );
    }

    protected Map<String, Object> prepareCommonTemplateData(String className, String packagePath) {
        Map<String, Object> data = new HashMap<>();
        data.put("basePackage", basePackage);
        data.put("packagePath", packagePath);
        data.put("className", className);
        data.put("language", language.getLanguageName());
        data.put("generatedFileHeaderComment", GeneratorHelper.generatedFileHeaderComment);
        data.put("importGeneratedAnnotation", GeneratorHelper.importGeneratedAnnotation);
        data.put("generatedAnnotation", GeneratorHelper.getGeneratedAnnotation(language));
        return data;
    }

    protected String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
