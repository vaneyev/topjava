package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MealMemoryDao implements MealDao {
    private final AtomicLong counter = new AtomicLong(0);
    private final Map<Long, Meal> meals = new ConcurrentHashMap<>();

    @Override
    public Meal getById(Long id) {
        return meals.get(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public Meal create(Meal meal) {
        long id = counter.getAndIncrement();
        Meal newMeal = new Meal(id, meal.getDateTime(), meal.getDescription(), meal.getCalories());
        meals.put(id, newMeal);
        return newMeal;
    }

    @Override
    public Meal update(Meal meal) {
        meals.replace(meal.getId(), meal);
        return meal;
    }

    @Override
    public Meal delete(Long id) {
        return meals.remove(id);
    }
}
