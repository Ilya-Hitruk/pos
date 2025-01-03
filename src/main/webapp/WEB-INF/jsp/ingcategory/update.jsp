<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Update</title>
    <%@ include file="/WEB-INF/jsp/styles/form-style.jsp"%>
</head>
<body>
<h1>Update category</h1>
<form action="${pageContext.request.contextPath}/ingredients/category/update" method="post">
    <input type="hidden" name="id" id="id" value="${param.id}">
    <label for="name">Name
        <input type="text" name="name" id="name" value="${requestScope.category.name}">
    </label>
    <div>
        <button type="submit">Update</button>
    </div>
    <br>
    <div>
        <button type="button"><a href="${pageContext.request.contextPath}/ingredients/categories">Back</a></button>
    </div>
    <%@ include file="/WEB-INF/jsp/errors.jsp"%>
</form>
</body>
</html>
