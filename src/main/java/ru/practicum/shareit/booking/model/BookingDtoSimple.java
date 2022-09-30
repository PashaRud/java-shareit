package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
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
