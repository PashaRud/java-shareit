package ru.practicum.shareit.request.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;
    @NotNull
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    private LocalDateTime created;
}
