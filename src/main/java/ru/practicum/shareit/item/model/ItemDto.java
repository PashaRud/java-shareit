package ru.practicum.shareit.item.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {


    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    @NotNull
    private String name;
    @NotNull
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull
    private Boolean available;
}
