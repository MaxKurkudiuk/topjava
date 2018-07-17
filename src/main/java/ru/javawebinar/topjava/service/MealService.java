package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface MealService {
    Meal get(int id, int userId) throws NotFoundException;

    void delete(int id, int userId) throws NotFoundException;

    Meal update(Meal meal, int userId) throws NotFoundException;

    Meal create(Meal meal, int userId);

    List<Meal> getAll(int userId);

    List<Meal> getAllBetweenTime(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId);
}