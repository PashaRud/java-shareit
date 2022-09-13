package ru.practicum.shareit.item.utilities;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.ItemDto;

public class ItemValidator {

    public static boolean itemIsValid(ItemDto item) {
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Название не может быть пустым.");
        }

        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ValidationException("Описание не может быть пустым.");
        }

        if (item.getAvailable() == null) {
            throw new ValidationException("Поле 'available' не может быть пустым");
        }
        return true;
    }

}
