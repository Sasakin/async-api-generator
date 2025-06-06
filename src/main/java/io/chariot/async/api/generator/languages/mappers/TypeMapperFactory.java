package io.chariot.async.api.generator.languages.mappers;

import io.chariot.async.api.generator.languages.TargetLanguage;
import java.util.EnumMap;
import java.util.Map;

/**
 * Фабрика для создания мапперов типов
 */
public class TypeMapperFactory {

    private static final Map<TargetLanguage, TypeMapper.TypeMappingStrategy> STRATEGIES = new EnumMap<>(TargetLanguage.class);

    static {
        STRATEGIES.put(TargetLanguage.JAVA, new JavaTypeMappingStrategy());
        STRATEGIES.put(TargetLanguage.KOTLIN, new KotlinTypeMappingStrategy());
        STRATEGIES.put(TargetLanguage.PYTHON, new PythonTypeMappingStrategy());
    }

    public static TypeMapper.TypeMappingStrategy getStrategy(TargetLanguage language) {
        return STRATEGIES.getOrDefault(language, STRATEGIES.get(TargetLanguage.JAVA));
    }
}
