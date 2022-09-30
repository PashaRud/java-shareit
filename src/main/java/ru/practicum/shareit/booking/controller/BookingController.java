package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingDtoSimple;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
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
                                    @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAll(userId, state);
    }


    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByItemOwnerId(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long bookingId, @RequestParam Boolean approved) {
        log.info("Получен запрос к эндпоинту /bookings, ID: " + bookingId);
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{id}")
    public BookingDto findById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Метод GET bookingId " + id);
        return bookingService.findById(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        bookingService.deleteById(id);
    }

}
