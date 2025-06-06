<#if generatedFileHeaderComment??>
${generatedFileHeaderComment}
</#if>

package ${basePackage}.${packagePath}

import ${importGeneratedAnnotation}

<#if generatedAnnotation??>
${generatedAnnotation}
</#if>
<#if isNumeric>
enum class ${className}(val value: Int) {
<#list enumValues as value>
    ${value.constantName}(${value.value})<#if value_has_next>,</#if>
</#list>
}
<#else>
enum class ${className}(val value: String) {
<#list enumValues as value>
    ${value.constantName}("${value.value}")<#if value_has_next>,</#if>
</#list>
}
</#if>