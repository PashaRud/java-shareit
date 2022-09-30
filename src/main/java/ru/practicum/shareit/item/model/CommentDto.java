package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;
    @NotNull
    @NotBlank(message = "Не может быть пустым")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
