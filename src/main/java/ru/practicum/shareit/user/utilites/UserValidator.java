package ru.practicum.shareit.user.utilites;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;

public class UserValidator {

    public static boolean userIsValid(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Введен некорректный email.");
        }
        return true;
    }
}
