<!--依赖bootstrap3-->
<#if msg??>
    <div id="msgDiv" class="alert-danger">${msg}</div>
    <#else>
    <!--可提供界面直接调用-->
    <div id="msgDiv" style="display: none" class="alert-danger">${msg}</div>
</#if>
<#if errorMsg??>
    <div id="dangerMsgDiv" class="alert-success">${errorMsg}</div>
    <#else>
    <!--可提供界面直接调用-->
    <div id="dangerMsgDiv" style="display: none" class="alert-success">${errorMsg}</div>
</#if>
