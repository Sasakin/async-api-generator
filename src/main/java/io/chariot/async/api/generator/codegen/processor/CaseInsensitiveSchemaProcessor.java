package io.chariot.async.api.generator.codegen.processor;

import io.chariot.async.api.generator.shema.components.Schema;
import io.chariot.async.api.generator.shema.components.SchemaProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.*;

import static io.chariot.async.api.generator.utils.GeneratorHelper.dehyphenizeClassName;

@RequiredArgsConstructor
public class CaseInsensitiveSchemaProcessor implements SchemaProcessor {

    private final Map<String, String> origToRenamedClasses = new HashMap<>();

    @Override
    public String processSchemaName(String originalName) {
        String processedName = capitalizeFirstLetter(originalName);
        return dehyphenizeClassName(origToRenamedClasses.getOrDefault(processedName, processedName));
    }

    @Override
    public SchemaProperties processAllOf(Schema schema, Map<String, Schema> allSchemas) {
        if (schema.getAllOf() == null) {
            return new SchemaProperties(
                    schema.getProperties() != null ? schema.getProperties() : Map.of(),
                    schema.getRequired() != null ? schema.getRequired() : List.of()
            );
        }

        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        for (Schema part : schema.getAllOf()) {
            // Рекурсивно разрешаем $ref
            Schema resolvedPart = resolveRef(part, allSchemas);
            SchemaProperties partProps = processAllOf(resolvedPart, allSchemas);

            properties.putAll(partProps.properties);
            required.addAll(partProps.required);

            if (part.getProperties() != null) {
                properties.putAll(part.getProperties());
            }
            if (part.getRequired() != null) {
                required.addAll(part.getRequired());
            }
        }

        if (schema.getProperties() != null) {
            properties.putAll(schema.getProperties());
        }
        if (schema.getRequired() != null) {
            required.addAll(schema.getRequired());
        }

        return new SchemaProperties(properties, required);
    }

    @Override
    public Schema resolveRef(Schema schema, Map<String, Schema> allSchemas) {
        if (schema.getRef() != null) {
            String refPath = schema.getRef().replace("#/components/schemas/", "");
            Schema refSchema = allSchemas.get(refPath);
            return refSchema != null ? resolveRef(refSchema, allSchemas) : schema;
        }
        return schema;
    }

    // В openapi-спеках имена схем (schema) регистрозависимые, поэтому в спеке можно задать два разных компонента
    // (класса) с именами InstanceTypeDTO и InstanceTypeDto. И в Linux с регистрозависимыми именами файлов
    // сгенерируются нормальные два файла с именами InstanceTypeDTO.java и InstanceTypeDto.java. Но в windows будут
    // проблемы: файлы регистронезависимые и первый созданный файл будет переписан последующими.
    // Поэтому в нашем генераторе (используемом преимущественно при разработке в среде Windows) предлагается пока
    // просто добавлять целочисленный индекс к классам (файлам), названия которых отличаются только регистром.
    public Map<String, String> renameCaseInsensitiveSchemas(Map<String, Schema> schemas) {
        var schemaCounter = new CaseInsensitiveMap<String, Integer>(schemas.size());
        Set.copyOf(schemas.keySet()).forEach(schemaName -> {
            var index = schemaCounter.merge(schemaName, 1, Integer::sum);
            if (index > 1) {
                var origSchema = schemas.remove(schemaName);
                var newSchemaName = schemaName + index;
                schemas.put(newSchemaName, origSchema);
                origToRenamedClasses.putIfAbsent(schemaName, newSchemaName);
            }
        });
        return origToRenamedClasses;
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
