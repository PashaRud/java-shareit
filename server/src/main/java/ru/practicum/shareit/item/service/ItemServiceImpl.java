package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDtoForItem;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.StorageException;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDtoForItem;
import static ru.practicum.shareit.item.mapper.CommentMapper.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;


    @Override
    public ItemDtoWithBooking findById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new StorageException("Вещи с Id = " + itemId + " нет в БД"));
        ItemDtoWithBooking itemDtoWithBooking = toItemDtoWithBooking(item);
        if (item.getOwner().getId() == userId) {
            createItemDtoWithBooking(itemDtoWithBooking);
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        if (!comments.isEmpty()) {
            itemDtoWithBooking.setComments(comments
                    .stream().map(comment -> toCommentDto(comment))
                    .collect(Collectors.toList())
            );
        }
        return itemDtoWithBooking;
    }

    @Override
    public List<ItemDtoWithBooking> findAll(long userId, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Переданы некорректные значения from and size");
        }
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        List<ItemDtoWithBooking> result = itemRepository.findByOwnerId(userId, pageable).stream()
                .map(item -> toItemDtoWithBooking(item))
                .collect(Collectors.toList());
        for (ItemDtoWithBooking itemDtoWithBooking : result) {
            createItemDtoWithBooking(itemDtoWithBooking);
            List<Comment> comments = commentRepository.findAllByItemId(itemDtoWithBooking.getId());
            if (!comments.isEmpty()) {
                itemDtoWithBooking.setComments(comments
                        .stream().map(comment -> toCommentDto(comment))
                        .collect(Collectors.toList()));
            }
        }
        result.sort(Comparator.comparing(ItemDtoWithBooking::getId));
        return result;
    }

    private void createItemDtoWithBooking(ItemDtoWithBooking itemDtoWithBooking) {
        List<Booking> lastBookings = bookingRepository
                .findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(itemDtoWithBooking.getId(),
                        LocalDateTime.now());
        if (!lastBookings.isEmpty()) {
            BookingDtoForItem lastBooking = toBookingDtoForItem(lastBookings.get(0));
            itemDtoWithBooking.setLastBooking(lastBooking);
        }
        List<Booking> nextBookings = bookingRepository
                .findBookingsByItemIdAndStartIsAfterOrderByStartDesc(itemDtoWithBooking.getId(),
                        LocalDateTime.now());
        if (!nextBookings.isEmpty()) {
            BookingDtoForItem nextBooking = toBookingDtoForItem(nextBookings.get(0));
            itemDtoWithBooking.setNextBooking(nextBooking);
        }
    }

    @Override
    public ItemDto save(ItemDto itemDto, long userId) {
        Item item = toItem(itemDto);
        item.setOwner(userRepository.findById(userId).orElseThrow(() ->
                new StorageException("Incorrect userId")));
        Long requestId = itemDto.getRequestId();
        if (requestId != null) {
            item.setItemRequest(itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new StorageException("Incorrect requestId")));
        }

        return toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto saveComment(long userId, long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new StorageException("Вещи с Id = " + itemId + " нет в БД"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new StorageException("Пользователя с Id = " + userId + " нет в БД"));
        List<Booking> bookings = bookingRepository
                .searchBookingByBookerIdAndItemIdAndEndIsBeforeAndStatus(userId, itemId,
                        LocalDateTime.now(), Status.APPROVED);
        if (bookings.isEmpty()) {
            throw new BookingException("Пользователь с Id = " + userId + " не брал в аренду вещь с Id = " + itemId);
        }
        Comment comment = toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        commentRepository.save(comment);
        return toCommentDto(comment);
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId, long id) {

        try {
            Item oldItem = itemRepository.findById(id).orElseThrow();

            if (oldItem.getOwner().getId() == userId) {

                if (itemDto.getName() != null) {
                    oldItem.setName(itemDto.getName());
                }
                if (itemDto.getDescription() != null) {
                    oldItem.setDescription(itemDto.getDescription());
                }
                if (itemDto.getAvailable() != null) {
                    oldItem.setAvailable(itemDto.getAvailable());
                }
                return toItemDto(itemRepository.save(oldItem));
            } else {
                throw new StorageException("Incorrect userId");
            }
        } catch (Exception e) {
            throw new StorageException("Incorrect ItemId OUT OF TRY");
        }
    }

    @Override
    public void deleteById(long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> searchItem(String text, int from, int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        if (!text.isBlank()) {
            return itemRepository.search(text, pageable)
                    .stream()
                    .filter(Item::getAvailable)
                    .map(item -> toItemDto(item))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}