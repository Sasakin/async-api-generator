<#if generatedFileHeaderComment??>
${generatedFileHeaderComment}
</#if>

package ${basePackage}.${packagePath}

import ${importGeneratedAnnotation}
import kotlinx.serialization.Serializable

<#list imports as imp>
import ${imp}
</#list>

<#if generatedAnnotation??>
${generatedAnnotation}
</#if>
@Serializable
data class ${className}(
<#list fields as field>

    <#if field.validationAnnotations??>${field.validationAnnotations}</#if><#if field.type == "LocalDateTime" || field.type == "OffsetDateTime" || field.type == "UUID">@Contextual</#if>
    val ${field.name}: ${field.type}<#if field.required == false>? = null</#if><#if field_has_next>,</#if>
</#list>
)