package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;

@ActiveProfiles(inheritProfiles = true, profiles = {Profiles.DATAJPA})
public class JdbcMealServiceTest extends MealServiceTest {
}
