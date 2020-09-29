package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> mealMap = new HashMap<>();
        Map<LocalDate, AtomicBoolean> excessMap = new HashMap<>();
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) calcExcess(caloriesPerDay, mealMap, excessMap, meal, startTime, endTime, result);
        return result;
    }

    private static void calcExcess(int caloriesPerDay,
                                   Map<LocalDate, Integer> mealMap,
                                   Map<LocalDate, AtomicBoolean> excessMap,
                                   UserMeal meal,
                                   LocalTime startTime,
                                   LocalTime endTime,
                                   List<UserMealWithExcess> result) {
        LocalDate localDate = meal.getDateTime().toLocalDate();
        int count = mealMap.merge(localDate, meal.getCalories(), Integer::sum);

        AtomicBoolean atomicExcess = excessMap.computeIfAbsent(localDate, ld -> new AtomicBoolean());
        atomicExcess.set(count > caloriesPerDay);

        if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
            result.add(new UserMealWithExcess(
                    meal.getDateTime(),
                    meal.getDescription(), meal.getCalories(),
                    atomicExcess));
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream().collect(new Collector<UserMeal, List<UserMealWithExcess>, List<UserMealWithExcess>>() {
            Map<LocalDate, Integer> mealMap = new HashMap<>();
            Map<LocalDate, AtomicBoolean> excessMap = new HashMap<>();

            @Override
            public Supplier<List<UserMealWithExcess>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<UserMealWithExcess>, UserMeal> accumulator() {
                return (c, meal) -> calcExcess(caloriesPerDay, mealMap, excessMap, meal, startTime, endTime, c);
            }

            @Override
            public BinaryOperator<List<UserMealWithExcess>> combiner() {
                return (c1, c2) -> {
                    c1.addAll(c2);
                    return c1;
                };
            }

            @Override
            public Function<List<UserMealWithExcess>, List<UserMealWithExcess>> finisher() {
                return c -> c;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT);
            }
        });
    }
}
