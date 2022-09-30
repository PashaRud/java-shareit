package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @Email(message = "Некорректный email")
    @NotBlank(message = "email не может быть пустым")
    private String email;
}
