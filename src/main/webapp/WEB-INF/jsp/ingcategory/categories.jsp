<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Ingredient categories</title>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>
    <%@ include file="/WEB-INF/jsp/styles/form-style.jsp"%>
    <%@ include file="/WEB-INF/jsp/styles/tables-style.jsp"%>
</head>
<body>
<h1>Ingredient categories</h1>
<button style="color: #007bff; float: right; width: 80px; height: 40px" type="button">
    <a href="${pageContext.request.contextPath}/ingredients/category/create">Create</a></button>
<table>
    <thead>
    <tr>
        <th>Name</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="category" items="${requestScope.categories}">
        <tr>
            <td>${category.name}</td>
            <td><button type="button"><a href="${pageContext.request.contextPath}/ingredients/category/update?id=${category.id}">Update</a></button> </td>
            <td><form method="post" action="${pageContext.request.contextPath}/ingredients/category/delete">
                <input type="hidden" name="id" value="${category.id}">
                <button type="submit">Delete</button>
            </form>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>
