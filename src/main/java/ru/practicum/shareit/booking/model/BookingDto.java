package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;
    @NotNull(message = "Поле не может быть пустым")
    private LocalDateTime start;
    @NotNull(message = "Поле не может быть пустым")
    private LocalDateTime end;
    private Item item;
    private User booker;
    private Status status;
}
