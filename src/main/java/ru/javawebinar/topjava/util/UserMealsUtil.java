package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
        Map<LocalDate, ProxyMeal> proxyMealMap = new HashMap<>();
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            LocalDate localDate = meal.getDateTime().toLocalDate();
            int count = mealMap.merge(localDate, meal.getCalories(), Integer::sum);
            if (count > caloriesPerDay) {
                ProxyMeal proxyMeal = proxyMealMap.getOrDefault(localDate, null);
                if (proxyMeal != null) {
                    proxyMeal.setExcess();
                    proxyMealMap.remove(localDate);
                }
            }
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                UserMealWithExcess mealWithExcess = new UserMealWithExcess(
                        meal.getDateTime(),
                        meal.getDescription(), meal.getCalories(),
                        count > caloriesPerDay);
                result.add(mealWithExcess);
                if (count <= caloriesPerDay) {
                    ProxyMeal proxyMeal = proxyMealMap.get(localDate);
                    proxyMealMap.put(localDate, new ProxyMeal(mealWithExcess, proxyMeal));
                }
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream().collect(new Collector<UserMeal, List<UserMealWithExcess>, List<UserMealWithExcess>>() {
            Map<LocalDate, Integer> mealMap = new HashMap<>();
            Map<LocalDate, ProxyMeal> proxyMealMap = new HashMap<>();

            @Override
            public Supplier<List<UserMealWithExcess>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<UserMealWithExcess>, UserMeal> accumulator() {
                return (c, meal) -> {
                    LocalDate localDate = meal.getDateTime().toLocalDate();
                    int count = mealMap.merge(localDate, meal.getCalories(), Integer::sum);
                    if (count > caloriesPerDay) {
                        ProxyMeal proxyMeal = proxyMealMap.getOrDefault(localDate, null);
                        if (proxyMeal != null) {
                            proxyMeal.setExcess();
                            proxyMealMap.remove(localDate);
                        }
                    }
                    if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                        UserMealWithExcess mealWithExcess = new UserMealWithExcess(
                                meal.getDateTime(),
                                meal.getDescription(), meal.getCalories(),
                                count > caloriesPerDay);
                        c.add(mealWithExcess);
                        if (count <= caloriesPerDay) {
                            ProxyMeal proxyMeal = proxyMealMap.get(localDate);
                            proxyMealMap.put(localDate, new ProxyMeal(mealWithExcess, proxyMeal));
                        }
                    }
                };
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

    private static class ProxyMeal {
        private ProxyMeal previousProxyMeal;
        private UserMealWithExcess meal;

        public ProxyMeal(UserMealWithExcess meal, ProxyMeal previousProxyMeal) {
            this.previousProxyMeal = previousProxyMeal;
            this.meal = meal;
        }

        public void setExcess() {
            meal.excess = true;
            if (previousProxyMeal != null) previousProxyMeal.setExcess();
        }
    }
}
