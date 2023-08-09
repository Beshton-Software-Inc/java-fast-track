<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>View Employee</title>
    <link href="<c:url value="/css/common.css"/>" rel="stylesheet" type="text/css">
</head>
<body>
<table>
    <thead>
    <tr>
        <th>first name</th>
        <th>last name</th>
        <th>role</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${Employee}" var="emp">
        <tr>
            <td>${emp.first_name}</td>
            <td>${emp.last_name}</td>
            <td>${emp.last_name}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>