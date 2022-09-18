package ru.practicum.shareit.user.DAO;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserDAOImpl implements UserDAO {
    private final Map<Long, User> users = new HashMap<>();
    private Long counter = 0L;

    @Override
    public User createUser(User user) {
        userIsValid(user);
        user.setId(++counter);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        availabilityCheck(userId);
        return users.get(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User updateUser(Long userId, User user) {
        availabilityCheck(userId);
        userIsValid(user);
        users.put(userId, user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        availabilityCheck(userId);
        users.remove(userId);
    }

    private void availabilityCheck(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(
                    String.format("Не найден юзер с id %d!", userId));
        }
    }

    public void userIsValid(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Введен некорректный email.");
        }
    }
}