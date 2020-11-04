package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private final CrudMealRepository crudRepository;
    private final CrudUserRepository crudUserRepository;

    public DataJpaMealRepository(CrudMealRepository crudRepository, CrudUserRepository crudUserRepository) {
        this.crudRepository = crudRepository;
        this.crudUserRepository = crudUserRepository;
    }

    @Transactional
    @Override
    public Meal save(Meal meal, int userId) {
        meal.setUser(crudUserRepository.getOne(userId));
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
    public Meal getWithUser(int id, int userId) {
        return crudRepository.getByUserIdWithUser(id, userId);
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
