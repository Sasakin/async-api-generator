package io.chariot.async.api.generator.languages.imports;

import io.chariot.async.api.generator.languages.TargetLanguage;
import java.util.Map;
import java.util.Set;

public class DtoImportHandler {

    /**
     * @deprecated Используйте {@link LanguageSupportFactory#getImportHandler(TargetLanguage)}
     */
    @Deprecated
    public static void addImportsForType(Set<String> imports, TargetLanguage language, String fieldType) {
        LanguageSupportFactory.getImportHandler(language).addImportsForType(imports, fieldType);
    }

    /**
     * @deprecated Используйте {@link LanguageSupportFactory#getImportHandler(TargetLanguage)}
     */
    @Deprecated
    public static void addImportsForArrayType(Set<String> imports, TargetLanguage language, String itemType) {
        LanguageSupportFactory.getImportHandler(language).addImportsForArrayType(imports, itemType);
    }

    /**
     * @deprecated Используйте {@link LanguageSupportFactory#getLanguageSupport(TargetLanguage)}
     */
    @Deprecated
    public static void addValidationImports(
            Set<String> imports,
            TargetLanguage language,
            Map<String, Object> propMap,
            boolean isRequired
    ) {
        LanguageSupportFactory.getLanguageSupport(language).addValidationImports(imports, propMap, isRequired);
    }

    /**
     * @deprecated Используйте {@link LanguageSupportFactory#getValidationGenerator(TargetLanguage)}
     */
    @Deprecated
    public static String generateValidationAnnotations(
            TargetLanguage language,
            Map<String, Object> propMap,
            boolean isRequired
    ) {
        return LanguageSupportFactory.getValidationGenerator(language)
                .generateValidationAnnotations(propMap, isRequired);
    }
}
