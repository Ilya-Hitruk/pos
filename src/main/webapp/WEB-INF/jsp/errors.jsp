
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty requestScope.errors}">
  <div class="error-messages">
    <c:forEach var="error" items="${requestScope.errors}">
      <span>${error.message}</span>
    </c:forEach>
  </div>
</c:if>
