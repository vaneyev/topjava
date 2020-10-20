package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int MEAL_ID = START_SEQ + 2;
    public static final int NOT_FOUND = 10;

    public static final Meal meal1 = new Meal(START_SEQ + 2, LocalDateTime.of(2020, 10, 19, 8, 30), "User Breakfast", 500);
    public static final Meal meal2 = new Meal(START_SEQ + 3, LocalDateTime.of(2020, 10, 19, 12, 35), "User Lunch", 1000);
    public static final Meal meal3 = new Meal(START_SEQ + 4, LocalDateTime.of(2020, 10, 19, 18, 10), "User Dinner", 1001);
    public static final Meal meal4 = new Meal(START_SEQ + 5, LocalDateTime.of(2020, 10, 20, 8, 30), "User Breakfast", 500);
    public static final Meal meal5 = new Meal(START_SEQ + 6, LocalDateTime.of(2020, 10, 20, 12, 35), "User Lunch", 500);
    public static final Meal meal6 = new Meal(START_SEQ + 7, LocalDateTime.of(2020, 10, 20, 18, 10), "User Dinner", 1000);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2020, 10, 10, 18, 10), "Admin Dinner", 1000);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(meal1);
        updated.setDateTime(LocalDateTime.of(2020, 10, 10, 15, 00));
        updated.setDescription("Updated meal");
        updated.setCalories(200);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingElementComparatorIgnoringFields().isEqualTo(expected);
    }

}
