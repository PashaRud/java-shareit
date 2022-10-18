package ru.practicum.shareit.booking.utils;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDtoSimple;

public class Utils {
    public static BookingDtoSimple toBookingDtoSimple(Booking booking) {
        return new BookingDtoSimple(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId());
    }
}
