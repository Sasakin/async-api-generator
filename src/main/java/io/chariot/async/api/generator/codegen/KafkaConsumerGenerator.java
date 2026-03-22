package io.chariot.async.api.generator.codegen;

import io.chariot.async.api.generator.shema.AsyncApiModel;
import io.chariot.async.api.generator.utils.GeneratorHelper;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static io.chariot.async.api.generator.utils.GeneratorHelper.dehyphenizeClassName;

@RequiredArgsConstructor
public class KafkaConsumerGenerator {
    private final String basePackage;
    public void generate(AsyncApiModel apiModel, String outputDir) {
        System.out.println("Generating Kafka Consumers with CloudEvent...");

        Map<String, Object> messages = apiModel.getComponents().getMessages();

        for (Map.Entry<String, Object> messageEntry : messages.entrySet()) {
            String messageName = messageEntry.getKey();
            Object message = messageEntry.getValue();

            if (message instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> messageMap = (Map<String, Object>) message;

                if (messageMap.containsKey("payload")) {
                    String payloadType = determinePayloadType(messageMap.get("payload"));
                    String channelName = findChannelForMessage(apiModel, messageName);

                    if (payloadType != null && channelName != null) {
                        generateConsumerClass(messageName, payloadType, channelName, outputDir);
                    }
                }
            }
        }
    }

    private String determinePayloadType(Object payload) {
        if (payload instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> payloadMap = (Map<String, Object>) payload;
            if (payloadMap.containsKey("$ref")) {
                return extractClassNameFromRef((String) payloadMap.get("$ref"));
            }
        } else if (payload instanceof String) {
            return extractClassNameFromRef((String) payload);
        }
        return null;
    }

    private String findChannelForMessage(AsyncApiModel apiModel, String messageName) {
        for (Map.Entry<String, AsyncApiModel.Channel> channelEntry : apiModel.getChannels().entrySet()) {
            AsyncApiModel.Channel channel = channelEntry.getValue();
            if (channel.getMessages() != null && channel.getMessages().containsKey(messageName)) {
                return channelEntry.getKey();
            }
        }
        return null;
    }

    private void generateConsumerClass(String messageName, String payloadType, String channelName, String outputDir) {
        String consumerName = generateConsumerClassName(channelName);
        String payloadClassName = dehyphenizeClassName(capitalizeFirstLetter(payloadType));

        StringBuilder sb = new StringBuilder();
        sb.append(GeneratorHelper.generatedFileHeaderComment);
        sb.append("package " + basePackage + ".consumers;\n");
        sb.append("import org.springframework.kafka.annotation.KafkaListener;\n");
        sb.append("import org.springframework.stereotype.Component;\n");
        sb.append("import org.springframework.kafka.support.Acknowledgment;\n");
        sb.append("import io.cloudevents.CloudEvent;\n");
        sb.append("import java.util.Map;\n");
        sb.append("import java.util.UUID;\n");
        sb.append(GeneratorHelper.importGeneratedAnnotation);
        sb.append("import " + basePackage + ".dto.").append(payloadClassName).append(";\n\n");

        sb.append("@Component\n");
        sb.append(GeneratorHelper.generatedAnnotationJava);
        sb.append("public class ").append(consumerName).append(" {\n\n");
        sb.append("    @KafkaListener(topics = \"").append(channelName).append("\")\n");
        sb.append("    public void consume(CloudEvent cloudEvent, Acknowledgment acknowledgment) {\n");
        sb.append("        try {\n");
        sb.append("            // Получаем CommonHeaders из расширений CloudEvent\n");
        sb.append("            Map<String, Object> extensions = CloudEventPrinter.getExtensions(cloudEvent);\n");
        sb.append("            CommonHeaders headers = new CommonHeaders();\n");
        sb.append("            headers.setRequestid(UUID.fromString((String) extensions.get(\"requestid\")));\n");
        sb.append("            headers.setResourceid(UUID.fromString((String) extensions.get(\"resourceid\")));\n");
        sb.append("            headers.setXiamctx((String) extensions.get(\"xiamctx\"));\n");
        sb.append("            headers.setXiamtkn((String) extensions.get(\"xiamtkn\"));\n");
        sb.append("            headers.setSessioncontext((String) extensions.get(\"sessioncontext\"));\n");
        sb.append("            headers.setClusterid((String) extensions.get(\"clusterid\"));\n\n");
        sb.append("            // Получаем payload напрямую из CloudEvent\n");
        sb.append("            ").append(payloadClassName).append(" payload = ConsumerUtils.getData(cloudEvent, ")
                .append(payloadClassName).append(".class);\n");
        sb.append("            \n");
        sb.append("            System.out.println(\"Received headers: \" + headers);\n");
        sb.append("            System.out.println(\"Received payload: \" + payload);\n");
        sb.append("            \n");
        sb.append("            // Обработка payload\n");
        sb.append("            \n");
        sb.append("            acknowledgment.acknowledge();\n");
        sb.append("        } catch (Exception e) {\n");
        sb.append("            System.err.println(\"Error processing message: \" + e.getMessage());\n");
        sb.append("            e.printStackTrace();\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("}\n");

        saveToFile(outputDir + File.separator + basePackage.replace(".", File.separator) + File.separator + "consumers" + File.separator, consumerName + ".java", sb.toString());
    }

    private String extractClassNameFromRef(String ref) {
        return ref.replace("#/components/schemas/", "")
                .replace("#/components/messages/", "");
    }

    private String generateConsumerClassName(String channelName) {
        return capitalizeFirstLetter(channelName.replaceAll("[^a-zA-Z0-9]", "")) + "Consumer";
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private void saveToFile(String directory, String fileName, String content) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(new File(dir, fileName))) {
            writer.write(content);
            System.out.println("File saved: " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving file: " + fileName);
            e.printStackTrace();
        }
    }
}
