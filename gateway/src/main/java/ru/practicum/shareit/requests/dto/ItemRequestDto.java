
package ru.practicum.shareit.requests.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;
    @NotNull
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    private LocalDateTime created;
}