package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.StorageException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    @Override
    public Collection<UserDto> getUsers() {
        return userDAO.getAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.toUserDto(userDAO.getUserById(userId));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userDAO.getUserById(userId);
        User.UserBuilder builder = user.toBuilder();
        if (userDto.getName() != null) {
            builder.name(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            checkUserForValid(userDto.getEmail());
            builder.email(userDto.getEmail());
        }
        return UserMapper.toUserDto(userDAO.updateUser(userId, builder.build()));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkUserForValid(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userDAO.createUser(user));
    }

    @Override
    public void deleteUser(Long userId) {
        userDAO.delete(userId);
    }

    private void checkUserForValid(String email) {
        if(email == null || email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Введен некорректный email.");
        }
        if(!userDAO.getAllUsers()
                .stream()
                .map(User::getEmail)
                .noneMatch(str -> str.equals(email))) {
            throw new StorageException(
                    String.format("Юзер с email %s уже существует.", email));
        }
    }
}
