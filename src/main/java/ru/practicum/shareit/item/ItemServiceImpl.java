package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.utilities.ItemValidator;
import ru.practicum.shareit.user.UserDAO;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDAO itemDAO;
    private final UserDAO userDAO;

    @Override
    public ItemDto addItem(ItemDto item, Long ownerId) {
        Item result = ItemMapper.toItem(item, ownerId);
        if (ItemValidator.itemIsValid(item) && userDAO.getUserById(ownerId) == null) {
            throw new NotFoundException("Проблемы с валидацией вещи или не найден хозяин вещи.");
        }

        return ItemMapper.toItemDto(itemDAO.addItem(ownerId, result));
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto item) {
        Item.ItemBuilder builder = itemDAO.getItem(itemId).toBuilder();
        if (!itemDAO.getItem(itemId).getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Пользователь не имеет права редактировать эту вещь!");
        }
        if (item.getName() != null) {
            builder.name(item.getName());
        }
        if (item.getDescription() != null) {
            builder.description(item.getDescription());
        }
        if (item.getAvailable() != null) {
            builder.available(item.getAvailable());
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