<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<jsp:useBean id="meals" scope="request" type="java.util.List"/>
<table>
    <thead>
    <tr>
        <td>
            Date
        </td>
        <td>
            Description
        </td>
        <td>
            Calories
        </td>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="meal" items="${meals}">
        <jsp:useBean id="dateTimeFormatter" scope="request" type="java.time.format.DateTimeFormatter"/>
        <tr style="color: ${meal.isExcess() ? "red" : "green"}">
            <td>
                    ${meal.getDateTime().format(dateTimeFormatter)}
            </td>
            <td>
                    ${meal.getDescription()}
            </td>
            <td>
                    ${meal.getCalories()}
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>