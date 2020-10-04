<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit meal</h2>
<jsp:useBean id="meal" scope="request" type="ru.javawebinar.topjava.model.Meal"/>
<form method="POST" action='meals' name="frmMeal">
    <input type="hidden" name="id" value="<c:out value="${meal.id}" />">
    <table>
        <tr>
            <td>Date :</td>
            <td>
                <label>
                    <input type="datetime-local" name="datetime" value="<c:out value="${meal.dateTime}" />"/>
                </label>
            </td>
        </tr>
        <tr>
            <td>Description :</td>
            <td>
                <label>
                    <input type="text" name="description" value="<c:out value="${meal.description}" />"/>
                </label>
            </td>
        </tr>
        <tr>
            <td>Calories :</td>
            <td>
                <label>
                    <input type="number" name="calories" value="<c:out value="${meal.calories}" />"/>
                </label>
            </td>
        </tr>
    </table>
    <input type="submit" value="Save"/>
    <input type="button" onclick="window.location='meals';return false;" value="Cancel"/>
</form>
</body>
</html>