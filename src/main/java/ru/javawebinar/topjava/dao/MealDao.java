package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    Meal getById(Long id);

    List<Meal> getAll();

    Meal create();

    void update(Meal meal);

    void delete(Long id);
}
