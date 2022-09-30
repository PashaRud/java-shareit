package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingDtoForItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoWithBooking {

    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    @NotNull
    private String name;
    @NotNull
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;
    private List<CommentDto> comments;
}
