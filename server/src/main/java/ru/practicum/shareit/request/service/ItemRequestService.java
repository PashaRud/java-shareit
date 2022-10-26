package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDtoWithItems> findAll(long userId);

    List<ItemRequestDtoWithItems> findAllRequests(long userId, int from, int size);

    ItemRequestDtoWithItems findByRequestId(long userId, long itemRequestId);
}

