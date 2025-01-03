<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%@ include file="/WEB-INF/jsp/header.jsp"%>
    <%@ include file="/WEB-INF/jsp/styles/form-style.jsp"%>
    <%@ include file="/WEB-INF/jsp/styles/tables-style.jsp"%>
    <title>Ingredients</title>

</head>
<body>
<h1>Ingredients</h1>
<button style="color: #007bff; float: right; width: 80px; height: 40px" type="button">
    <a href="${pageContext.request.contextPath}/ingredient/create">Create</a></button>
<table>
    <thead>
    <tr>
        <th>Name</th>
        <th>Category</th>
        <th>Measure</th>
        <th>Price for Unit</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="ingredient" items="${requestScope.ingredients}">
        <tr>
            <td>${ingredient.name}</td>
            <td>${ingredient.categoryIngredientName}</td>
            <td>${ingredient.measure}</td>
            <td>${ingredient.priceForUnit}</td>
            <td><button type="button"><a href="${pageContext.request.contextPath}/ingredient/update?id=${ingredient.id}">Update</a></button> </td>
            <td><form method="post" action="${pageContext.request.contextPath}/ingredient/delete">
                <input type="hidden" name="id" value="${ingredient.id}">
                <button type="submit">Delete</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>