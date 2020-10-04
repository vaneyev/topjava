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
import java.time.format.DateTimeFormatter;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final int CALORIES_PER_DAY = 2000;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");
    private static MealDao meals;
    private static final String MEALS_PAGE = "/meals.jsp";
    private static final String MEAL_PAGE = "/meal.jsp";

    @Override
    public void init() throws ServletException {
        super.init();
        String dao = getServletConfig().getInitParameter("dao");
        if ("memory".equalsIgnoreCase(dao)) {
            meals = new MealMemoryDao();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("delete".equalsIgnoreCase(action)) {
            meals.delete(Long.parseLong(request.getParameter("id")));
            request.setAttribute("meals", MealsUtil.filteredByStreams(meals.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
            request.setAttribute("dateTimeFormatter", dateTimeFormatter);
            log.debug("forward to meals");
            request.getRequestDispatcher(MEALS_PAGE).forward(request, response);
        } else if ("create".equalsIgnoreCase(action)) {
            request.setAttribute("meal", meals.create());
            log.debug("forward to meal");
            request.getRequestDispatcher(MEAL_PAGE).forward(request, response);
        } else if ("update".equalsIgnoreCase(action)) {
            request.setAttribute("meal", meals.getById(Long.parseLong(request.getParameter("id"))));
            log.debug("forward to meal");
            request.getRequestDispatcher(MEAL_PAGE).forward(request, response);
        } else {
            request.setAttribute("meals", MealsUtil.filteredByStreams(meals.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
            request.setAttribute("dateTimeFormatter", dateTimeFormatter);
            log.debug("forward to meals");
            request.getRequestDispatcher(MEALS_PAGE).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        long id = Long.parseLong(request.getParameter("id"));
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("datetime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        meals.update(new Meal(id, dateTime, description, calories));
        request.setAttribute("meals", MealsUtil.filteredByStreams(meals.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
        request.setAttribute("dateTimeFormatter", dateTimeFormatter);
        log.debug("forward to meals");
        request.getRequestDispatcher(MEALS_PAGE).forward(request, response);
    }
}
