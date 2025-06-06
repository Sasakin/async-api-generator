package io.chariot.async.api.generator.languages.imports;

import io.chariot.async.api.generator.languages.TargetLanguage;
import java.util.EnumMap;
import java.util.Map;

/**
 * Фабрика для создания языковой поддержки
 */
public class LanguageSupportFactory {

    private static final Map<TargetLanguage, BaseLanguageSupport> SUPPORT_CACHE = new EnumMap<>(TargetLanguage.class);

    static {
        SUPPORT_CACHE.put(TargetLanguage.JAVA, new JavaImportSupport());
        SUPPORT_CACHE.put(TargetLanguage.KOTLIN, new KotlinImportSupport());
    }

    public static ImportHandler getImportHandler(TargetLanguage language) {
        return SUPPORT_CACHE.getOrDefault(language, SUPPORT_CACHE.get(TargetLanguage.JAVA));
    }

    public static ValidationAnnotationGenerator getValidationGenerator(TargetLanguage language) {
        return SUPPORT_CACHE.getOrDefault(language, SUPPORT_CACHE.get(TargetLanguage.JAVA));
    }

    public static BaseLanguageSupport getLanguageSupport(TargetLanguage language) {
        return SUPPORT_CACHE.getOrDefault(language, SUPPORT_CACHE.get(TargetLanguage.JAVA));
    }
}
