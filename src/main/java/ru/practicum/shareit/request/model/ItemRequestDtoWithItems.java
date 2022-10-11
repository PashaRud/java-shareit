package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.model.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDtoWithItems {

    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
