package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserDAO {
    User createUser(User user);

    User getUserById(Long userId);

    Collection<User> getAllUsers();

    User updateUser(Long userId, User user);

    void delete(Long userId);
}
