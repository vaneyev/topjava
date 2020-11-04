package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    @Query("""
                 SELECT m FROM Meal m 
                 WHERE m.user.id=:userId AND m.dateTime >= :startDateTime AND m.dateTime < :endDateTime ORDER BY m.dateTime DESC
            """)
    List<Meal> findBetweenHalfOpen(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, @Param("userId") int userId);

    @Query("SELECT m FROM Meal m WHERE m.user.id = ?1 ORDER BY m.dateTime desc")
    List<Meal> findByUserId(int userId);

    @Query("SELECT m FROM Meal m WHERE m.id = ?1 AND m.user.id = ?2")
    Meal getByUserId(int id, int userId);

    @Query("SELECT m FROM Meal m LEFT JOIN FETCH m.user WHERE m.id = ?1 AND m.user.id = ?2")
    Meal getByUserIdWithUser(int id, int userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Meal m WHERE m.id=:id AND m.user.id = :userId")
    int delete(@Param("id") int id, @Param("userId") int userId);
}
