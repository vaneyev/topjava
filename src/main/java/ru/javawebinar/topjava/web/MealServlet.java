package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MealMemoryDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final int caloriesPerDay = 2000;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");
    private static final String mealsForwardPage = "/meals.jsp";
    private static final String mealsRedirectPage = "meals";
    private static final String mealPage = "/meal.jsp";
    private MealDao meals;

    @Override
    public void init() {
        meals = new MealMemoryDao();
        meals.create(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        meals.create(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        meals.create(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        meals.create(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        meals.create(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        meals.create(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        meals.create(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    private enum MealAction {
         CREATE, REQUEST, UPDATE, DELETE,
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MealAction action;
        try {
            action = MealAction.valueOf(request.getParameter("action").toUpperCase());
        } catch (Exception exception) {
            action = MealAction.REQUEST;
        }
        switch (action) {
            case REQUEST:
                request.setAttribute("meals", MealsUtil.filteredByStreams(meals.getAll(), LocalTime.MIN, LocalTime.MAX, caloriesPerDay));
                request.setAttribute("dateTimeFormatter", dateTimeFormatter);
                log.debug("request meals");
                request.getRequestDispatcher(mealsForwardPage).forward(request, response);
                break;
            case DELETE:
                Long id = parseId(request);
                meals.delete(id);
                log.debug("delete the meal with id " + id);
                response.sendRedirect(mealsRedirectPage);
                break;
            case CREATE:
                request.setAttribute("meal", new Meal(null, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 0));
                log.debug("forward to a new meal");
                request.getRequestDispatcher(mealPage).forward(request, response);
                break;
            case UPDATE:
                Meal meal = meals.getById(parseId(request));
                request.setAttribute("meal", meal);
                log.debug("forward to the meal with id " + meal.getId());
                request.getRequestDispatcher(mealPage).forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Long id = request.getParameter("id").isEmpty() ? null : parseId(request);
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("datetime")).truncatedTo(ChronoUnit.MINUTES);
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        Meal meal = new Meal(id, dateTime, description, calories);
        if (id == null) {
            meal = meals.create(meal);
            log.debug("create the meal with id " + meal.getId());
        } else {
            meal = meals.update(meal);
            if (meal == null) {
                log.error("the meal with id " + id + " was not updated");
            } else {
                log.debug("update the meal with id " + meal.getId());
            }
        }
        response.sendRedirect(mealsRedirectPage);
    }

    private Long parseId(HttpServletRequest request) {
        return Long.parseLong(request.getParameter("id"));
    }
}
