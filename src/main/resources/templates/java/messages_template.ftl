<#if generatedFileHeaderComment??>
${generatedFileHeaderComment}
</#if>

package ${basePackage}.${packagePath};

<#list imports as imp>
import ${imp};
</#list>

<#if importGeneratedAnnotation??>
import ${importGeneratedAnnotation}
</#if>
import lombok.Data;

@Data
<#if generatedAnnotation??>
${generatedAnnotation}
</#if>
public class ${className} {
<#if headersType??>
    private ${headersType} headers;
</#if>
<#if payloadType??>
    private ${payloadType} payload;
</#if>
}