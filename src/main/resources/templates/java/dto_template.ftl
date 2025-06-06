<#-- dto_template.ftl -->
<#if generatedFileHeaderComment??>
${generatedFileHeaderComment}
</#if>

package ${basePackage}.${packagePath};

<#if importGeneratedAnnotation??>
import ${importGeneratedAnnotation}
</#if>

<#list imports as imp>
import ${imp};
</#list>

<#if imports?size gt 0>
</#if>
import lombok.Data;

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

<#if hasBuilder>

    public static ${className}Builder builder() {
        return new ${className}Builder();
    }

    public static class ${className}Builder {
<#list fields as field>
        private ${field.type} ${field.name};

        public ${className}Builder ${field.name}(${field.type} ${field.name}) {
            this.${field.name} = ${field.name};
            return this;
        }
</#list>

        public ${className} build() {
            ${className} obj = new ${className}();
<#list fields as field>
            obj.set${field.name?cap_first}(${field.name});
</#list>
            return obj;
        }
    }
</#if>
}