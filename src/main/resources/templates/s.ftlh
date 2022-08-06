<#import "parts/common.ftlh" as c>
<#import "parts/pager.ftlh" as p>
<@c.page>
    <@p.pager url page/>
    <div class="container mt-5">
        <p>Get your photo here</p>
        <div class="row">
            <#list page.content as image>
                <div class="col-md-3">
                    <img class="img-fluid img-thumbnail" src="/img${image.filename}"/>
                </div>
            <#else>
                No message
            </#list>
        </div>
    </div>
    <@p.pager url page/>
</@c.page>