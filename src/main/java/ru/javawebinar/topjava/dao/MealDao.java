package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    Meal getById(Long id);

    List<Meal> getAll();

    Meal create(Meal meal);

    Meal update(Meal meal);

    Meal delete(Long id);
}
