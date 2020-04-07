<%--
  Created by IntelliJ IDEA.
  User: yan
  Date: 2020/3/26
  Time: 16:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="/saveFile" method="post" enctype="multipart/form-data">
    上传文件：
    <input type="file" name="file">
    <br>
    <input type="submit" value="上传">
</form>
</body>
</html>
