package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.user1Meals.forEach(meal -> this.save(meal, 1));
        MealsUtil.user2Meals.forEach(meal -> this.save(meal, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        Map<Integer, Meal> meals = repository.computeIfAbsent(userId, key -> new ConcurrentHashMap<>());
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meals.put(meal.getId(), meal);
            return meal;
        }
        return meals.computeIfPresent(meal.getId(), (key, value) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        Map<Integer, Meal> meals = repository.get(userId);
        return meals == null ? null : meals.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Map<Integer, Meal> meals = repository.get(userId);
        return meals == null ? null : meals.get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getByPredicate(userId, meal -> true);
    }

    @Override
    public List<Meal> getByDates(int userId, LocalDate startDate, LocalDate endDate) {
        return getByPredicate(userId, meal -> DateTimeUtil.isBetweenDates(meal.getDate(), startDate, endDate));
    }

    private List<Meal> getByPredicate(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> meals = repository.get(userId);
        return meals == null ? null : repository.get(userId).values().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

