<#-- messagetrait_template.ftl -->
<#if generatedFileHeaderComment??>
${generatedFileHeaderComment}
</#if>

package ${basePackage}.${packagePath};

<#list imports as imp>
import ${imp};
</#list>

<#if imports?size gt 0>
</#if>
import lombok.Data;

<#if importGeneratedAnnotation??>
import ${importGeneratedAnnotation}
</#if>

@Data
<#if generatedAnnotation??>
${generatedAnnotation}
</#if>
public class ${className} {
 <#list fields as field>
    <#if field.validationAnnotations??>
    ${field.validationAnnotations}
    </#if>
    private ${field.type} ${field.name};<#if field_has_next>

    </#if>
 </#list>
}