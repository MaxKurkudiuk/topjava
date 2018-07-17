package ru.javawebinar.topjava.repository.mock;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.repository.mock.InMemoryUserRepositoryImpl.ADMIN_ID;
import static ru.javawebinar.topjava.repository.mock.InMemoryUserRepositoryImpl.USER_ID;

public class InMemoryMealRepositoryImpl implements MealRepository {
    private final Comparator<Meal> MEAL_COMPARE = (m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime());
    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(MEAL -> save(MEAL, USER_ID));
        save(new Meal(LocalDateTime.of(2018, Month.JULY, 15, 13, 0), "Адмін обід", 510), ADMIN_ID);
        save(new Meal(LocalDateTime.of(2018, Month.JULY, 15, 19, 0), "Адмін вечеря", 510), ADMIN_ID);
    }

    @Override
    public Meal save(Meal meal, int userId) {
        Integer mealId = meal.getId();

        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
        } else if (get(mealId, userId) == null){
            return null;
        }
        // treat case: update, but absent in storage
        Map<Integer, Meal> meals = repository.computeIfAbsent(userId, ConcurrentHashMap::new);
        meals.put(mealId, meal);
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        Map<Integer, Meal> meals = repository.get(userId);
        return meals != null && meals.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Map<Integer, Meal> meals = repository.get(userId);
        return meals == null ? null : meals.get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return repository.get(userId).values().stream().sorted(MEAL_COMPARE).collect(Collectors.toList());
    }

    @Override
    public Collection<Meal> getAll(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return getAll(userId).stream()
                .filter(meal -> DateTimeUtil.isBetween(meal.getDateTime() ,startDateTime, endDateTime))
                .sorted(MEAL_COMPARE)
                .collect(Collectors.toList());
    }
}

