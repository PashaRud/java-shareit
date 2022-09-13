package ru.practicum.shareit.item.utilities;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemDto;


public class ItemValidator {

    public static boolean itemIsValid(ItemDto item) {
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Item name cannot be empty");
        }

        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ValidationException("Item description cannot be empty");
        }

        if (item.getAvailable() == null) {
            throw new ValidationException("Item availability cannot be empty");
        }
        return true;
    }

}
