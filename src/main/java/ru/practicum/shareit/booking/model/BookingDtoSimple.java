package ru.practicum.shareit.booking.model;

import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoSimple {

    private Long id;
    @NotNull(message = "Поле не может быть пустым")
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull(message = "Поле не может быть пустым")
    @FutureOrPresent
    private LocalDateTime end;
    private Long itemId;
}
