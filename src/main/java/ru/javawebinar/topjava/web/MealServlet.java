package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private ConfigurableApplicationContext appCtx;
    private MealRestController controller;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        log.info("Bean definition names: {}", Arrays.toString(appCtx.getBeanDefinitionNames()));
        controller = appCtx.getBean(MealRestController.class);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String filter = request.getParameter("filter");
        if (filter.equals("true")) {
            Map<String, String> parameters = new HashMap<>();
            String startDate = request.getParameter("startdate");
            if (startDate != null && !startDate.isEmpty()) {
                parameters.put("startdate", startDate);
            }
            String endDate = request.getParameter("enddate");
            if (endDate != null && !endDate.isEmpty()) {
                parameters.put("enddate", endDate);
            }
            String startTime = request.getParameter("starttime");
            if (startTime != null && !startTime.isEmpty()) {
                parameters.put("starttime", startTime);
            }
            String endTime = request.getParameter("endtime");
            if (endTime != null && !endTime.isEmpty()) {
                parameters.put("endtime", endTime);
            }
            response.sendRedirect("meals?" + parameters.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("&")));
        } else {
            String id = request.getParameter("id");

            Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id), LocalDateTime.parse(request.getParameter("dateTime")),
                    request.getParameter("description"),
                    Integer.parseInt(request.getParameter("calories")));

            log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
            if (meal.isNew()) {
                controller.create(meal);
            } else {
                controller.update(meal, meal.getId());
            }
            response.sendRedirect("meals");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                controller.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        controller.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                LocalDate startDate;
                try {
                    startDate = LocalDate.parse(request.getParameter("startdate"));
                    request.setAttribute("startdate", startDate);
                } catch (Exception ex) {
                    startDate = LocalDate.MIN;
                }
                LocalDate endDate;
                try {
                    endDate = LocalDate.parse(request.getParameter("enddate"));
                    request.setAttribute("enddate", endDate);
                } catch (Exception ex) {
                    endDate = LocalDate.MAX;
                }
                LocalTime startTime;
                try {
                    startTime = LocalTime.parse(request.getParameter("starttime"));
                    request.setAttribute("starttime", startTime);
                } catch (Exception ex) {
                    startTime = LocalTime.MIN;
                }
                LocalTime endTime;
                try {
                    endTime = LocalTime.parse(request.getParameter("endtime"));
                    request.setAttribute("endtime", endTime);
                } catch (Exception ex) {
                    endTime = LocalTime.MAX;
                }
                request.setAttribute("meals", controller.getByDatesAndTimes(startDate, endDate, startTime, endTime));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    @Override
    public void destroy() {
        super.destroy();
        appCtx.close();
    }
}
