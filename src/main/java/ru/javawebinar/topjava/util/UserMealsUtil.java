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

        mealsTo = filteredByCyclesOptional2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreamsOptional2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));

        mealsTo = filteredByCyclesOriginalOptional2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreamsOriginalOptional2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
        for (UserMeal meal : meals) {
            caloriesPerDayMap.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                        caloriesPerDayMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap =
                meals.stream()
                        .collect(Collectors.toMap(meal -> meal.getDateTime().toLocalDate(), UserMeal::getCalories, Integer::sum));
        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                        caloriesPerDayMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByCyclesOptional2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
        Map<LocalDate, AtomicBoolean> excessPerDayMap = new HashMap<>();
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            calcExcess(caloriesPerDay, caloriesPerDayMap, excessPerDayMap, meal, startTime, endTime, result);
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreamsOptional2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(new Collector<UserMeal, List<UserMealWithExcess>, List<UserMealWithExcess>>() {
                    Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
                    Map<LocalDate, AtomicBoolean> excessPerDayMap = new HashMap<>();

                    @Override
                    public Supplier<List<UserMealWithExcess>> supplier() {
                        return ArrayList::new;
                    }

                    @Override
                    public BiConsumer<List<UserMealWithExcess>, UserMeal> accumulator() {
                        return (list, meal) -> calcExcess(caloriesPerDay, caloriesPerDayMap, excessPerDayMap, meal, startTime, endTime, list);
                    }

                    @Override
                    public BinaryOperator<List<UserMealWithExcess>> combiner() {
                        return (list1, list2) -> {
                            list1.addAll(list2);
                            return list1;
                        };
                    }

                    @Override
                    public Function<List<UserMealWithExcess>, List<UserMealWithExcess>> finisher() {
                        return Function.identity();
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return EnumSet.of(Characteristics.CONCURRENT);
                    }
                });
    }

    private static void calcExcess(int caloriesPerDay, Map<LocalDate, Integer> mealMap, Map<LocalDate, AtomicBoolean> excessMap, UserMeal meal, LocalTime startTime, LocalTime endTime, List<UserMealWithExcess> result) {
        LocalDate localDate = meal.getDateTime().toLocalDate();
        int count = mealMap.merge(localDate, meal.getCalories(), Integer::sum);

        AtomicBoolean atomicExcess = excessMap.computeIfAbsent(localDate, ld -> new AtomicBoolean());
        atomicExcess.set(count > caloriesPerDay);

        if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
            result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), atomicExcess));
        }
    }

    public static List<UserMealWithExcess> filteredByCyclesOriginalOptional2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
        Map<LocalDate, ProxyMeal> proxyMealMap = new HashMap<>();
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            calcExcessOriginal(result, meal, caloriesPerDayMap, caloriesPerDay, proxyMealMap, startTime, endTime);
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreamsOriginalOptional2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream().collect(new Collector<UserMeal, List<UserMealWithExcess>, List<UserMealWithExcess>>() {
            Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
            Map<LocalDate, ProxyMeal> proxyMealMap = new HashMap<>();

            @Override
            public Supplier<List<UserMealWithExcess>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<UserMealWithExcess>, UserMeal> accumulator() {
                return (list, meal) -> calcExcessOriginal(list, meal, caloriesPerDayMap, caloriesPerDay, proxyMealMap, startTime, endTime);
            }


            @Override
            public BinaryOperator<List<UserMealWithExcess>> combiner() {
                return (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                };
            }

            @Override
            public Function<List<UserMealWithExcess>, List<UserMealWithExcess>> finisher() {
                return Function.identity();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT);
            }
        });
    }

    private static void calcExcessOriginal(List<UserMealWithExcess> result, UserMeal meal, Map<LocalDate, Integer> caloriesPerDayMap, int caloriesPerDay, Map<LocalDate, ProxyMeal> proxyMealMap, LocalTime startTime, LocalTime endTime) {
        LocalDate localDate = meal.getDateTime().toLocalDate();
        int count = caloriesPerDayMap.merge(localDate, meal.getCalories(), Integer::sum);
        if (count > caloriesPerDay) {
            ProxyMeal proxyMeal = proxyMealMap.remove(localDate);
            if (proxyMeal != null) {
                proxyMeal.setExcess();
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

    private static class ProxyMeal {
        private ProxyMeal previousProxyMeal;
        private UserMealWithExcess meal;

        public ProxyMeal(UserMealWithExcess meal, ProxyMeal previousProxyMeal) {
            this.previousProxyMeal = previousProxyMeal;
            this.meal = meal;
        }

        public void setExcess() {
            meal.setExcess(true);
            if (previousProxyMeal != null) {
                previousProxyMeal.setExcess();
            }
        }
    }
}
