<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Create category</title>
    <%@ include file="/WEB-INF/jsp/styles/form-style.jsp"%>
</head>
<body>
<form action="${pageContext.request.contextPath}/ingredients/category/create" method="post">
    <label for="name"> Category name
        <input type="text" name="name" id="name" required>
    </label><br>

    <div>
        <button type="submit">Create</button>
    </div>
    <br>
    <div>
        <button type="button"><a href="${pageContext.request.contextPath}/ingredients/categories">Back</a></button>
    </div>

    <c:if test="${not empty requestScope.errors}">
        <div style="color: red">
            <c:forEach var="error" items="${requestScope.errors}">
                <span>${error.message}</span>
            </c:forEach>
        </div>
    </c:if>
</form>
</body>
</html>
