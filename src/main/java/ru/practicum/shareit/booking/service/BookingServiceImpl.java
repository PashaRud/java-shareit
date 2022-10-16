package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingDtoSimple;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.ItemException;
import ru.practicum.shareit.exception.StorageException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto findById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new StorageException("Бронирования с Id = " + bookingId + " нет в БД"));
        if (booking.getBooker().getId() != userId
                && booking.getItem().getOwner().getId() != userId) {
            throw new StorageException("Incorrect userId");
        }
        return toBookingDto(booking);

    }

    @Override
    public List<BookingDto> findAll(long userId, String state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new StorageException("Incorrect userId"));
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());
        try {
            switch (Status.valueOf(state)) {
                case ALL:
                    return bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable)
                            .stream().map(booking -> toBookingDto(booking)).collect(Collectors.toList());
                case CURRENT:
                    return bookingRepository.findCurrentBookingsByBookerIdOrderByStartDesc(userId,
                                    LocalDateTime.now(), pageable).stream().map(booking -> toBookingDto(booking))
                            .collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findBookingsByBookerIdAndEndIsBeforeOrderByStartDesc(userId,
                                    LocalDateTime.now(), pageable).stream().map(booking -> toBookingDto(booking))
                            .collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId,
                                    LocalDateTime.now(), pageable)
                            .stream()
                            .map(booking -> toBookingDto(booking)).collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findBookingsByBookerIdAndStatus(userId,
                                    Status.WAITING, pageable)
                            .stream()
                            .map(booking -> toBookingDto(booking)).collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findBookingsByBookerIdAndStatus(userId,
                                    Status.REJECTED, pageable)
                            .stream()
                            .map(booking -> toBookingDto(booking)).collect(Collectors.toList());
                default:
                    throw new BookingException("Unknown state: " + state);
            }
        } catch (IllegalArgumentException e) {
            throw new BookingException("Unknown state: " + state);
        }
    }

    @Override
    public BookingDto save(BookingDtoSimple bookingDtoSimple, long userId) {
        if (bookingDtoSimple.getEnd().isBefore(bookingDtoSimple.getStart())) {
            throw new BookingException("Incorrect end time");
        }
        Booking booking = fromSimpleToBooking(bookingDtoSimple);
        booking.setBooker(userRepository.findById(userId).orElseThrow());
        Item item = itemRepository.findById(bookingDtoSimple.getItemId())
                .orElseThrow(() -> new StorageException("Вещи с Id = "
                        + bookingDtoSimple.getItemId() + " нет в БД"));
        if (!item.getAvailable()) {
            throw new ItemException("Вещь с Id = " + bookingDtoSimple.getItemId() +
                    " не доступна");
        }
        if (item.getOwner().getId() == userId) {
            throw new StorageException("Владелец вещи не может забронировать свою вещь");
        } else {
            booking.setItem(item);
            return toBookingDto(bookingRepository.save(booking));
        }
    }

    @Override
    public BookingDto update(long bookingId, BookingDto bookingDto) {
        BookingDto oldBookingDto = toBookingDto(bookingRepository.findById(bookingId).orElseThrow());
        if (bookingDto.getStart() != null) {
            oldBookingDto.setStart(bookingDto.getStart());
        }
        if (bookingDto.getEnd() != null) {
            oldBookingDto.setEnd(bookingDto.getEnd());
        }
        if (bookingDto.getItem() != null) {
            oldBookingDto.setItem(bookingDto.getItem());
        }
        if (bookingDto.getBooker() != null) {
            oldBookingDto.setBooker(bookingDto.getBooker());
        }
        if (bookingDto.getStatus() != null) {
            oldBookingDto.setStatus(bookingDto.getStatus());
        }
        return toBookingDto(bookingRepository.save(toBooking(oldBookingDto)));
    }

    @Override
    public void deleteById(long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public BookingDto approve(long userId, long bookingId, Boolean approved) {
        BookingDto bookingDto = toBookingDto(bookingRepository.findById(bookingId).orElseThrow());
        if (bookingDto.getItem().getOwner().getId() != userId) {
            throw new StorageException("Подтвердить бронирование может только владелец вещи");
        }
        if (bookingDto.getStatus().equals(Status.APPROVED)) {
            throw new BookingException("Бронирование уже подтверждено");
        }
        if (approved == null) {
            throw new BookingException("Необходимо указать approved");
        } else if (approved) {
            bookingDto.setStatus(Status.APPROVED);
            return toBookingDto(bookingRepository.save(toBooking(bookingDto)));
        } else {
            bookingDto.setStatus(Status.REJECTED);
            return toBookingDto(bookingRepository.save(toBooking(bookingDto)));
        }
    }

    @Override
    public List<BookingDto> findAllByItemOwnerId(long userId, String state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new StorageException("Некорректный id"));
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());
        List<BookingDto> result = bookingRepository.searchBookingByItemOwnerId(userId, pageable).stream()
                .map(booking -> toBookingDto(booking)).collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new StorageException("У пользователя нет вещей");
        }
        try {
            switch (Status.valueOf(state)) {
                case ALL:
                    result.sort(Comparator.comparing(BookingDto::getStart).reversed());
                    return result;
                case CURRENT:
                    return bookingRepository.findCurrentBookingsByItemOwnerIdOrderByStartDesc(userId,
                                    LocalDateTime.now(), pageable).stream().map(booking -> toBookingDto(booking))
                            .collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findBookingsByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId,
                                    LocalDateTime.now(), pageable).stream().map(booking -> toBookingDto(booking))
                            .collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.searchBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId,
                                    LocalDateTime.now(), pageable).stream().map(booking -> toBookingDto(booking))
                            .collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findBookingsByItemOwnerIdOrderByStartDesc(userId, pageable).stream()
                            .filter(booking -> booking.getStatus().equals(Status.WAITING))
                            .map(booking -> toBookingDto(booking)).collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findBookingsByItemOwnerIdOrderByStartDesc(userId, pageable).stream()
                            .filter(booking -> booking.getStatus().equals(Status.REJECTED))
                            .map(booking -> toBookingDto(booking)).collect(Collectors.toList());
                default:
                    throw new BookingException("Unknown state: " + state);
            }
        } catch (IllegalArgumentException e) {
            throw new BookingException("Unknown state: " + state);
        }
    }
}
