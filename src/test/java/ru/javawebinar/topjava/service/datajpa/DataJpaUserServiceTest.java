package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserServiceTest;

import java.util.Collections;

import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.MealTestData.meals;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(profiles = {Profiles.DATAJPA})
public class DataJpaUserServiceTest extends UserServiceTest {

    @Test
    public void getWithMeal() {
        User user =  service.getWithMeal(USER_ID);
        USER_MATCHER.assertMatch(user, UserTestData.user);
        MEAL_MATCHER.assertMatch(user.getMeals(), meals);
    }

    @Test
    public void getWithMealWithoutMeal() {
        User user =  service.getWithMeal(USER1_ID);
        USER_MATCHER.assertMatch(user, UserTestData.user1);
        MEAL_MATCHER.assertMatch(user.getMeals(), Collections.EMPTY_LIST);
    }
}
