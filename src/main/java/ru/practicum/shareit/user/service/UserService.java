package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getUsers();

    UserDto getUser(Long userId);

    UserDto updateUser(Long userId, UserDto userDto);

    UserDto createUser(UserDto userDto);

    void deleteUser(Long userId);
}