<#setting number_format="#.##">
<#import "spring.ftl" as spring />
<#assign ctx = springMacroRequestContext.getContextPath()>
<#assign form = JspTaglibs["http://www.springframework.org/tags/form"]>
<#assign h=JspTaglibs["/web-helper"]>

<#macro show_flush_message>
    <#if notice??>
    <div id="actionMessage" class="alert alert-success">
        <span>${notice}</span>&nbsp;&nbsp;[<a href="#" onclick="jQuery('#actionMessage').hide();return false;">hide</a>]
    </div>
    </#if>
    <#if error??>
    <div id="actionMessage" class="alert alert-danger">
        <span>${error}</span>&nbsp;&nbsp;[<a href="#" onclick="jQuery('#actionMessage').hide();return false;">hide</a>]
    </div>
    </#if>
</#macro>