package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.StorageException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto findById(long userId) {
        return userMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new StorageException("Пользователя с Id = " + userId + " нет в БД")));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> toUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto save(UserDto userDto) {
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        UserDto oldUserDto = findById(userId);
        if (userDto.getName() != null) {
            oldUserDto.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            oldUserDto.setEmail(userDto.getEmail());
        }
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(oldUserDto)));
    }

    @Override
    public void deleteById(long userId) {
        userRepository.deleteById(userId);
    }

}
