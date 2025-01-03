<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <%@ include file="header.jsp"%>
    <title>Error</title>
</head>
<body>
<h1>Oooops. Looks like something went wrong</h1>
  <div style="color: crimson">
    <span>${requestScope.error}</span>
  </div>
  </body>
</html>
