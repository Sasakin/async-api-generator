package io.chariot.async.api.generator.languages.imports;

import io.chariot.async.api.generator.languages.TargetLanguage;

import java.util.Set;

public class MessageImportHandler {
    /**
     * Добавляет языково-специфичные импорты для сообщений
     *
     * @param imports      множество импортов, в которое будут добавлены необходимые импорты
     * @param language     целевой язык программирования
     * @param basePackage  базовый пакет для формирования путей импортов
     * @param headersType  тип заголовков сообщения
     * @param payloadType  тип полезной нагрузки сообщения
     */
    public static void addMessageImports(Set<String> imports, TargetLanguage language,
                                         String basePackage, String headersType, String payloadType) {
        switch (language) {
            case JAVA:
            case KOTLIN:
                handleJavaKotlinImports(imports, basePackage, headersType, payloadType);
                break;
            default:
                // Для неподдерживаемых языков ничего не делаем
                break;
        }
    }

    private static void handleJavaKotlinImports(Set<String> imports, String basePackage,
                                                String headersType, String payloadType) {
        if (headersType != null) {
            imports.add(basePackage + ".messagetraits." + headersType);
        }
        if (payloadType != null) {
            imports.add(basePackage + ".dto." + payloadType);
        }
    }
}
