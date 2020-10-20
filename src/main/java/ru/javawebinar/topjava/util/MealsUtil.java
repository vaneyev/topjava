package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealsUtil {
    public static final int DEFAULT_CALORIES_PER_DAY = 2000;

    public static List<Meal> getUserMeals() {
        return Arrays.asList(
                new Meal(null, LocalDateTime.of(2020, 10, 19, 8, 30), "User Breakfast", 500),
                new Meal(null, LocalDateTime.of(2020, 10, 19, 12, 35), "User Lunch", 1000),
                new Meal(null, LocalDateTime.of(2020, 10, 19, 18, 10), "User Dinner", 1001),
                new Meal(null, LocalDateTime.of(2020, 10, 20, 8, 30), "User Breakfast", 500),
                new Meal(null, LocalDateTime.of(2020, 10, 20, 12, 35), "User Lunch", 500),
                new Meal(null, LocalDateTime.of(2020, 10, 20, 18, 10), "User Dinner", 1000)
        );
    }

    public static List<Meal> getAdminMeals() {
        return Arrays.asList(
                new Meal(null, LocalDateTime.of(2020, 10, 20, 8, 30), "Admin Breakfast", 500),
                new Meal(null, LocalDateTime.of(2020, 10, 20, 12, 35), "Admin Lunch", 500),
                new Meal(null, LocalDateTime.of(2020, 10, 20, 18, 10), "Admin Dinner", 1000)
        );
    }

    public static List<MealTo> getTos(Collection<Meal> meals, int caloriesPerDay) {
        return filterByPredicate(meals, caloriesPerDay, meal -> true);
    }

    public static List<MealTo> getFilteredTos(Collection<Meal> meals, int caloriesPerDay, LocalTime startTime, LocalTime endTime) {
        return filterByPredicate(meals, caloriesPerDay, meal -> Util.isBetweenHalfOpen(meal.getTime(), startTime, endTime));
    }

    public static List<MealTo> filterByPredicate(Collection<Meal> meals, int caloriesPerDay, Predicate<Meal> filter) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(
                        Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories))
//                      Collectors.toMap(Meal::getDate, Meal::getCalories, Integer::sum)
                );

        return meals.stream()
                .filter(filter)
                .map(meal -> createTo(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static MealTo createTo(Meal meal, boolean excess) {
        return new MealTo(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }
}
