package io.chariot.async.api.generator;

import io.chariot.async.api.generator.codegen.*;
import io.chariot.async.api.generator.languages.TargetLanguage;
import io.chariot.async.api.generator.parser.YamlParser;
import io.chariot.async.api.generator.shema.AsyncApiModel;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class AsyncApiGenerator {
    public static String appVersion;
    public static Instant generationTs;

    public static void main(String[] args) {
        // Парсинг аргументов
        if (args.length < 3) {
            throw new IllegalArgumentException(
                    "Usage: AsyncApiGenerator <input-yaml> <output-dir> <base-package> [--generate-producers] [--generate-consumers]"
            );
        }

        String inputYaml = args[0];
        String outputDir = args[1];
        String basePackage = args[2];
        boolean generateProducers = false;
        boolean generateConsumers = false;
        TargetLanguage language = TargetLanguage.JAVA;

        // Проверка флагов
        for (int i = 3; i < args.length; i++) {
            if (args[i].startsWith("--language=")) {
                String langArg = args[i].substring("--language=".length()).toLowerCase();
                try {
                    language = TargetLanguage.valueOf(langArg.toUpperCase());
                } catch (IllegalArgumentException e) {
                    StringBuilder availableLanguages = new StringBuilder();
                    for (TargetLanguage tl : TargetLanguage.values()) {
                        if (availableLanguages.length() > 0) availableLanguages.append(", ");
                        availableLanguages.append(tl.name().toLowerCase());
                    }

                    throw new IllegalArgumentException(
                            "Unsupported language: " + langArg + ". Supported languages: " + availableLanguages
                    );
                }
                continue;
            }

            switch (args[i]) {
                case "--generate-producers":
                    generateProducers = true;
                    break;
                case "--generate-consumers":
                    generateConsumers = true;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown argument: " + args[i]);
            }
        }

        AsyncApiModel apiModel = YamlParser.parse(inputYaml);

        appVersion = AsyncApiGenerator.class.getPackage().getImplementationVersion();
        if (appVersion == null) {
            appVersion = "x.x.x"; // для тестов (пойдет такой формат)
        }
        generationTs = Instant.now();

        printWelcomeMesage();

        DtoGeneratorFactory.create(
                apiModel,
                basePackage,
                language
        ).generate(apiModel.getComponents().getSchemas(), outputDir);

        MessageTraitsGeneratorFactory.create(
                basePackage,
                language
        ).generate(apiModel.getComponents().getMessageTraits(), outputDir);

        new MessagesGenerator(basePackage, language).generate(apiModel.getComponents().getMessages(), outputDir);

        // Условная генерация Kafka компонентов
        if (generateProducers) {
            new KafkaProducerGenerator(basePackage).generate(apiModel, outputDir);
        }
        if (generateConsumers) {
            new KafkaConsumerGenerator(basePackage).generate(apiModel, outputDir);
        }

        printFinalMessage();
    }

    private static void printFinalMessage() {
        System.out.println("Chariot AsyncAPI Generator stopped.");
    }

    private static void printWelcomeMesage() {
        System.out.println("""

                ╔════════════════════════════════════════════╗
                ║     Chariot AsyncAPI Generator              ║
                ║     version: %-16s                    ║
                ╚════════════════════════════════════════════╝
                """.formatted(appVersion));
    }
}
