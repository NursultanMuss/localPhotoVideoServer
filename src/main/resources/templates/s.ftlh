<!DOCTYPE HTML>
<html>
<head>
    <title>Getting Started: Serving Web Content</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<p>Get your photo here</p>
<div>
    <form method="POST" enctype="multipart/form-data">
        <table>
            <tr><td>File to upload:</td>
                <td><input type="file" name="file" /></td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <input type="submit" value="Upload" />
                </td>
            </tr>
        </table>
    </form>
</div>
<div >
    <tbody>
    <#list images as image>
        <img src="/img${image.filename}"/>
    <#else>
        No message
    </#list>
    </tbody>
</div>
</body>
</html>