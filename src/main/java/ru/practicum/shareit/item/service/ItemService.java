package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    ItemDto getItem(Long itemId);

    Collection<ItemDto> getAllItemsByOwner(Long ownerId);

    Collection<ItemDto> search(String search);
}
