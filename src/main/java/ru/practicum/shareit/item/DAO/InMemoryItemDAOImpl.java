package ru.practicum.shareit.item.DAO;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.DAO.ItemDAO;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemDAOImpl implements ItemDAO {

    private final Map<Long, Item> items = new HashMap<>();
    private Long counter = 0L;

    @Override
    public Item addItem(Long ownerId, Item item) {
        item.setId(++counter);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        availabilityCheck(itemId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item getItem(Long itemId) {
        availabilityCheck(itemId);
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getAllItemsByOwner(Long ownerId) {
        return items.values()
                .stream()
                .filter(i -> i.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> search(String search) {
        if (search.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(i -> i.getName().toLowerCase().contains(search.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void availabilityCheck(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException(
                    String.format("Нет items с ID %d!", itemId));
        }
    }
}
