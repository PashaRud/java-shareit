package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingDtoSimple;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDtoSimple bookingDtoSimple,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос к эндпоинту /bookings. ItemId: " + bookingDtoSimple.getItemId());
        return bookingService.save(bookingDtoSimple, userId);
    }

    @GetMapping
    public List<BookingDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestParam(defaultValue = "ALL") String state,
                                    @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                    @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Получен запрос к эндпоинту /bookings. Метод findAll. UserId: " + userId);
        return bookingService.findAll(userId, state, from, size);
    }


    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = "ALL") String state,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Получен запрос к эндпоинту /owner. Метод findAllByOwner. UserId: " + userId);
        return bookingService.findAllByItemOwnerId(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long bookingId, @RequestParam Boolean approved) {
        log.info("Получен запрос к эндпоинту /bookings/{bookingId}, UserID: " + bookingId);
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{id}")
    public BookingDto findById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Метод GET bookingId " + id);
        return bookingService.findById(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        log.info("Метод DELETE для ID " + id);
        bookingService.deleteById(id);
    }

}
