package io.chariot.async.api.generator.codegen;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.languages.imports.MessageImportHandler;
import io.chariot.async.api.generator.utils.FileUtils;
import io.chariot.async.api.generator.utils.GeneratorHelper;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static io.chariot.async.api.generator.utils.GeneratorHelper.dehyphenizeClassName;

@RequiredArgsConstructor
public class MessagesGenerator {
    private final String basePackage;
    private final TargetLanguage language;
    private final Configuration freemarkerConfig;

    public MessagesGenerator(String basePackage, TargetLanguage language) {
        this.basePackage = basePackage;
        this.language = language;
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        freemarkerConfig.setClassForTemplateLoading(MessagesGenerator.class, "/templates");
    }

    public void generate(Map<String, Object> messages, String outputDir) {
        if (messages == null || messages.isEmpty()) {
            System.out.println("No messages to generate, skipping.");
            return;
        }

        String packagePath = "messages";
        File dir = new File(outputDir + File.separator + basePackage.replace(".", File.separator) + File.separator +  packagePath.replace(".", File.separator));
        dir.mkdirs();

        for (Map.Entry<String, Object> messageEntry : messages.entrySet()) {
            String messageName = messageName(messageEntry.getKey());
            Object message = messageEntry.getValue();

            if (message instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> messageMap = (Map<String, Object>) message;

                if (messageMap.containsKey("payload") && messageMap.containsKey("traits")) {
                    Object payload = messageMap.get("payload");
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> traits = (List<Map<String, String>>) messageMap.get("traits");

                    String payloadType = null;
                    if (payload instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> payloadMap = (Map<String, Object>) payload;
                        if (payloadMap.containsKey("$ref")) {
                            payloadType = extractClassNameFromRef((String) payloadMap.get("$ref"));
                        }
                    } else if (payload instanceof String) {
                        payloadType = extractClassNameFromRef((String) payload);
                    }

                    String headersType = null;
                    for (Map<String, String> trait : traits) {
                        if (trait.containsKey("$ref")) {
                            headersType = extractClassNameFromRef(trait.get("$ref"));
                        }
                    }

                    generateKafkaRecordClass(messageName, headersType, payloadType, dir, packagePath);
                }
            }
        }
    }

    private void generateKafkaRecordClass(String className, String headersType, String payloadType, File dir, String packagePath) {
        // Подготовка данных для шаблона
        Map<String, Object> data = new HashMap<>();
        data.put("basePackage", basePackage);
        data.put("packagePath", packagePath);
        data.put("className", className + "Message");
        data.put("headersType", headersType);
        data.put("payloadType", payloadType);
        data.put("generatedFileHeaderComment", GeneratorHelper.generatedFileHeaderComment);
        data.put("importGeneratedAnnotation", GeneratorHelper.importGeneratedAnnotation);
        data.put("generatedAnnotation", GeneratorHelper.getGeneratedAnnotation(language));
        data.put("imports", new ArrayList<>());
        data.put("language", language.getLanguageName());

        // Сборка импортов
        Set<String> imports = new LinkedHashSet<>();
        MessageImportHandler.addMessageImports(imports, language, basePackage, headersType, payloadType);
        data.put("imports", new ArrayList<>(imports));

        // Генерация через Freemarker
        try {
            String templatePath = language.getTemplatePath("messages_template.ftl");
            Template template = freemarkerConfig.getTemplate(templatePath);

            StringWriter writer = new StringWriter();
            template.process(data, writer);

            String fileName = className + "Message" + language.getFileExtension();
            FileUtils.writeToFile(dir, fileName, writer.toString());
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("Error generating Message: " + className, e);
        }
    }

    private String messageName(String key) {
        return dehyphenizeClassName(capitalizeFirstLetter(key));
    }

    private String extractClassNameFromRef(String ref) {
        String className = ref
                .replace("#/components/schemas/", "")
                .replace("#/components/messageTraits/", "")
                .replace("#/components/messages/", "");
        return dehyphenizeClassName(capitalizeFirstLetter(className));
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
