<#-- messages_template.ftl -->
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
<#if headersType??>
    val headers: ${headersType}<#if payloadType??>,</#if>
</#if>
<#if payloadType??>
    <#if payloadType == "LocalDateTime" || payloadType == "OffsetDateTime" || payloadType == "UUID">@Contextual</#if>
    val payload: ${payloadType}
</#if>
)