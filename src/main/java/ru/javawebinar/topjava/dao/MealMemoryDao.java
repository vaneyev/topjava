package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MealMemoryDao implements MealDao {
    private static final AtomicLong counter = new AtomicLong(Long.MIN_VALUE);
    private static final Map<Long, Meal> meals = new ConcurrentHashMap<>();

    static {
        meals.put(counter.get(), new Meal(counter.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        meals.put(counter.get(), new Meal(counter.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        meals.put(counter.get(), new Meal(counter.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        meals.put(counter.get(), new Meal(counter.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        meals.put(counter.get(), new Meal(counter.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        meals.put(counter.get(), new Meal(counter.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        meals.put(counter.get(), new Meal(counter.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    public Meal getById(Long id) {
        return meals.get(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public Meal create() {
        long id = counter.getAndIncrement();
        Meal meal = new Meal(id, LocalDateTime.now(), "", 0);
        meals.put(id, meal);
        return meal;
    }

    @Override
    public void update(Meal meal) {
        meals.replace(meal.getId(), meal);
    }

    @Override
    public void delete(Long id) {
        meals.remove(id);
    }
}
