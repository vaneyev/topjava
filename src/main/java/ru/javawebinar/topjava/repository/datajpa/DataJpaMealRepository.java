package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private final CrudMealRepository crudRepository;

    public DataJpaMealRepository(CrudMealRepository crudRepository) {
        this.crudRepository = crudRepository;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        User user = new User();
        user.setId(userId);
        meal.setUser(user);
        if (meal.isNew()) {
            return crudRepository.save(meal);
        }
        return get(meal.id(), userId) != null ? crudRepository.save(meal) : null;

    }

    @Override
    public boolean delete(int id, int userId) {
        return crudRepository.delete(id, userId) > 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudRepository.getByUserId(id, userId);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findByUserId(userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.findBetweenHalfOpen(startDateTime, endDateTime, userId);
    }
}
