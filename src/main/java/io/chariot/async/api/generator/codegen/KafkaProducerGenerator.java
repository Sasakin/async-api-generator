package io.chariot.async.api.generator.codegen;

import io.chariot.async.api.generator.shema.AsyncApiModel;
import io.chariot.async.api.generator.shema.components.Ref;
import io.chariot.async.api.generator.utils.GeneratorHelper;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.chariot.async.api.generator.utils.GeneratorHelper.dehyphenizeClassName;

@RequiredArgsConstructor
public class KafkaProducerGenerator {
    private final String basePackage;
    public void generate(AsyncApiModel apiModel, String outputDir) {
        System.out.println("Generating Kafka Producers...");

        StringBuilder sb = new StringBuilder();
        sb.append(GeneratorHelper.generatedFileHeaderComment);

        for (Map.Entry<String, AsyncApiModel.Channel> channelEntry : apiModel.getChannels().entrySet()) {
            String channelName = channelEntry.getKey();
            AsyncApiModel.Channel channel = channelEntry.getValue();

            System.out.println("Channel: " + channel.getAddress());

            if (channel.getMessages() != null) {
                for (Map.Entry<String, Ref> messageEntry : channel.getMessages().entrySet()) {
                    String messageName = messageEntry.getKey();
                    String ref = messageEntry.getValue().getRef();

                    String dtoClassName = dehyphenizeClassName(extractDtoClassName(ref));
                    String producerName = channelName + "Producer";

                    sb.setLength(0);
                    sb.append(GeneratorHelper.generatedAnnotationJava);
                    sb.append("public class ").append(producerName).append(" {\n");
                    sb.append("    private final KafkaTemplate<String, ").append(dtoClassName).append("> kafkaTemplate;\n\n");
                    sb.append("    public ").append(producerName).append("(KafkaTemplate<String, ").append(dtoClassName).append("> kafkaTemplate) {\n");
                    sb.append("        this.kafkaTemplate = kafkaTemplate;\n");
                    sb.append("    }\n\n");
                    sb.append("    public void send(").append(dtoClassName).append(" message) {\n");
                    sb.append("        kafkaTemplate.send(\"").append(channel.getAddress()).append("\", message);\n");
                    sb.append("    }\n");
                    sb.append("}\n");

                    System.out.println(sb);
                }
            }
        }
    }

    private String extractDtoClassName(String ref) {
        return ref.replace("#/components/schemas/", "");
    }
}
