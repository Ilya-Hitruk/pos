<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%@ include file="/WEB-INF/jsp/styles/form-style.jsp"%>
    <title>Create Ingredient</title>

</head>
<body>
<h1>Create Ingredient</h1>
<form action="${pageContext.request.contextPath}/ingredient/create" method="post">
    <label for="name">Name
        <input type="text" name="name" id="name" required>
    </label>

    <label for="category">Category
        <select name="category" id="category">
            <c:forEach var="category" items="${requestScope.categories}">
                <option value="${category.name}" >${category.name}</option>
            </c:forEach>
        </select>
    </label>

    <label for="measure">Measure
        <select name="measure" id="measure" required>
            <c:forEach var="measure" items="${requestScope.measures}">
                <option value="${measure}">${measure}</option>
            </c:forEach>
        </select>
    </label>

    <div>
        <button type="submit">Create</button>
    </div>
    <br>
    <div>
        <button type="button">
            <a href="${pageContext.request.contextPath}/ingredients/category/create">Create category</a>
        </button>
    </div>
    <br>
    <div>
        <button type="button"><a href="${pageContext.request.contextPath}/ingredients">Back</a></button>
    </div>

    <c:if test="${not empty requestScope.errors}">
        <div class="error-messages">
            <c:forEach var="error" items="${requestScope.errors}">
                <span>${error.message}</span>
            </c:forEach>
        </div>
    </c:if>
</form>
</body>
</html>