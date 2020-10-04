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
    private MealDao meals;
    private static final String mealsForwardPage = "/meals.jsp";
    private static final String mealsRedirectPage = "meals";
    private static final String mealPage = "/meal.jsp";

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("meals", MealsUtil.filteredByStreams(meals.getAll(), LocalTime.MIN, LocalTime.MAX, caloriesPerDay));
            request.setAttribute("dateTimeFormatter", dateTimeFormatter);
            log.debug("forward to meals");
            request.getRequestDispatcher(mealsForwardPage).forward(request, response);
            return;
        }
        switch (action) {
            case "delete":
                meals.delete(parseId(request));
                request.setAttribute("meals", MealsUtil.filteredByStreams(meals.getAll(), LocalTime.MIN, LocalTime.MAX, caloriesPerDay));
                request.setAttribute("dateTimeFormatter", dateTimeFormatter);
                log.debug("redirect to meals");
                response.sendRedirect(mealsRedirectPage);
                break;
            case "create":
                request.setAttribute("meal", new Meal(null, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 0));
                log.debug("forward to meal");
                request.getRequestDispatcher(mealPage).forward(request, response);
                break;
            case "update":
                request.setAttribute("meal", meals.getById(parseId(request)));
                log.debug("forward to meal");
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
        if (id == null) {
            meals.create(new Meal(id, dateTime, description, calories));
        } else {
            meals.update(new Meal(id, dateTime, description, calories));
        }
        request.setAttribute("meals", MealsUtil.filteredByStreams(meals.getAll(), LocalTime.MIN, LocalTime.MAX, caloriesPerDay));
        request.setAttribute("dateTimeFormatter", dateTimeFormatter);
        log.debug("redirect to meals");
        response.sendRedirect(mealsRedirectPage);
    }

    private Long parseId(HttpServletRequest request) {
        return Long.parseLong(request.getParameter("id"));
    }
}
