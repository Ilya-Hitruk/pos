<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%@ include file="/WEB-INF/jsp/styles/form-style.jsp"%>

    <title>Update</title>
</head>
<body>
<h1>Update Ingredient</h1>
<form action="${pageContext.request.contextPath}/ingredient/update" method="post">
    <label><input type="hidden" name="id" value="${param.id}"></label>
    <label for="name">Name
        <input type="text" name="name" id="name" value="${requestScope.ingredient.name}" required>
    </label>

    <label for="category">Category
        <select name="category" id="category">
            <c:forEach var="category" items="${requestScope.categories}">
                <option value="${category.name}"
                        <c:if test="${category.name == requestScope.ingredient.categoryIngredientName}">selected</c:if>>
                        ${category.name}
                </option>
            </c:forEach>
        </select>
    </label>

    <label for="measure">Measure
        <select name="measure" id="measure" required>
            <c:forEach var="measure" items="${requestScope.measures}">
                <option value="${measure}"
                        <c:if test="${measure == requestScope.ingredient.measure}">selected</c:if>>
                ${measure}
                </option>
            </c:forEach>
        </select>
    </label>

    <div>
        <button type="submit">Update</button>
    </div>
    <br>
    <div>
        <button type="button"><a href="${pageContext.request.contextPath}/ingredients">Back</a></button>
    </div>

    <%@ include file="/WEB-INF/jsp/errors.jsp"%>
</form>
</body>
</html>