package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.DAO.ItemDAO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.utilities.ItemValidator;
import ru.practicum.shareit.user.DAO.UserDAO;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDAO itemDAO;
    private final UserDAO userDAO;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        Item result = ItemMapper.toItem(itemDto, ownerId);
        if (ItemValidator.itemIsValid(itemDto) && userDAO.getUserById(ownerId) == null) {
            throw new NotFoundException("Проблемы с валидацией вещи или не найден хозяин вещи.");
        }

        return ItemMapper.toItemDto(itemDAO.addItem(ownerId, result));
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        Item.ItemBuilder builder = itemDAO.getItem(itemId).toBuilder();
        if (!itemDAO.getItem(itemId).getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Пользователь не имеет права редактировать эту вещь!");
        }
        if (itemDto.getName() != null) {
            builder.name(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            builder.description(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            builder.available(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemDAO.updateItem(itemId, builder.build()));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(itemDAO.getItem(itemId));
    }

    @Override
    public Collection<ItemDto> getAllItemsByOwner(Long ownerId) {
        return itemDAO.getAllItemsByOwner(ownerId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String searchQuery) {
        return itemDAO.search(searchQuery)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}