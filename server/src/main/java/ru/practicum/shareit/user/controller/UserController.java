package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Получен запрос к эндпоинту: /users, метод findAll");
        return userService.findAll();
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту: /users, Пользователь: Имя: " + userDto.getName());
        return userService.save(userDto);
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable long id) {
        log.info("Метод GET с user " + id);
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id) {
        log.info("Получен запрос к эндпоинту: DELETE /users/id , userId " + id);
        userService.deleteById(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserDto userDto) {
        log.info("Метод PATCH с user " + id);
        return userService.update(id, userDto);
    }
}