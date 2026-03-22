${generatedFileHeaderComment}
package ${basePackage}.${packagePath};

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.HashMap;
import ${importGeneratedAnnotation}
import com.fasterxml.jackson.annotation.JsonValue;

@Getter
@RequiredArgsConstructor
${generatedAnnotation}
public enum ${className} {
<#list enumValues as value>
    ${value.constantName}(${isNumeric?then("${value.value}", "\"${value.value}\"")})<#if value_has_next>,</#if>
</#list>;

    private final ${firstValueType} value;

    private static final Map<${firstValueType}, ${className}> VALUE_MAP = new HashMap<>();

    static {
        for (${className} enumValue : ${className}.values()) {
            VALUE_MAP.put(enumValue.value, enumValue);
        }
    }

    @JsonValue
    public ${firstValueType} getValue() {
        return value;
    }

    public static ${className} fromValue(${firstValueType} value) {
        ${className} result = VALUE_MAP.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Invalid value: " + value);
        }
        return result;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}