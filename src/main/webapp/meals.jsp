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
<a href="meals?action=create">Add Meal</a>
<table>
    <tr>
        <th>
            Date
        </th>
        <th>
            Description
        </th>
        <th>
            Calories
        </th>
    </tr>
    <jsp:useBean id="meals" scope="request" type="java.util.List<ru.javawebinar.topjava.model.MealTo>"/>
    <c:forEach var="meal" items="${meals}">
        <jsp:useBean id="dateTimeFormatter" scope="request" type="java.time.format.DateTimeFormatter"/>
        <tr style="color: ${meal.excess ? "red" : "green"}">
            <td>
                    ${meal.dateTime.format(dateTimeFormatter)}
            </td>
            <td>
                    ${meal.description}
            </td>
            <td>
                    ${meal.calories}
            </td>
            <td>
                <a href="meals?action=update&id=${meal.id}">Update</a>
            </td>
            <td>
                <a href="meals?action=delete&id=${meal.id}">Delete</a>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>