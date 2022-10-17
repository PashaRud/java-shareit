package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.StorageException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;

import static ru.practicum.shareit.request.mapper.ItemRequestMapper.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper mapper;
    private final UserRepository userRepository;


    @Override
    public ItemRequestDto save(ItemRequestDto itemRequestDto, long userId) {
        ItemRequest itemRequest = toItemRequest(itemRequestDto);
        itemRequest.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new StorageException("Некорректный userId")));
        return toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoWithItems> findAll(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new StorageException("Пользователя с Id " +
                        userId + " нет в БД"));
        return itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userId)
                .stream()
                .map(mapper::toItemRequestDtoWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoWithItems> findAllRequests(long userId, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Переданы некорректные значения from and size");
        }
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("created"));
        userRepository.findById(userId).orElseThrow(() ->
                new StorageException("Пользователя с Id = " + userId + " нет в БД"));
        return itemRequestRepository.findAll(pageable)
                .stream()
                .filter(itemRequest -> itemRequest.getUser().getId() != userId)
                .map(mapper::toItemRequestDtoWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoWithItems findByRequestId(long userId, long itemRequestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new StorageException("Пользователя с Id = " + userId + " нет в БД"));
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(() ->
                new StorageException("Запроса с Id = " + itemRequestId + " нет в БД"));
        return mapper.toItemRequestDtoWithItems(itemRequest);
    }
}
