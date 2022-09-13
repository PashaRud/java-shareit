package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemDAO {

    Item addItem(Long ownerId, Item item);

    Item updateItem(Long itemId, Item item);

    Item getItem(Long itemId);

    Collection<Item> getAllItemsByOwner(Long ownerId);

    Collection<Item> search(String search);
}
