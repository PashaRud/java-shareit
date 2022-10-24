package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BookingException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                              Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10")
                                              Integer size) {
        BookingState stateParam = BookingState.from(state)
                .orElseThrow(() -> new BookingException("Unknown state: " + state));
        log.info("Метод Get booking = state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, stateParam, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByItemOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @RequestParam(defaultValue = "ALL") String state,
                                                           @PositiveOrZero
                                                           @RequestParam(name = "from", defaultValue = "0")
                                                           Integer from,
                                                           @Positive @RequestParam(name = "size", defaultValue = "10")
                                                           Integer size) {
        BookingState stateParam = BookingState.from(state)
                .orElseThrow(() -> new BookingException("Unknown state: " + state));
        log.info("Метод Get booking = state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookingsByItemOwnerId(userId, stateParam, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Метод Post booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Метод Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long bookingId, @RequestParam Boolean approved) {
        log.info("Получен запрос к эндпоинту: 'PATCH/bookings', Подтверждение бронирование: ID: {}",
                bookingId);
        return bookingClient.approveBook(userId, bookingId, approved);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        bookingClient.deleteById(id);
    }
}