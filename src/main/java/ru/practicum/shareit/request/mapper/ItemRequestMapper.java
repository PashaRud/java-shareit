package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;


@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final ItemRepository itemRepository;

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                LocalDateTime.now(),
                null);
    }

    public ItemRequestDtoWithItems toItemRequestDtoWithItems(ItemRequest itemRequest) {
        ItemRequestDtoWithItems itemRequestDtoWithItems = new ItemRequestDtoWithItems(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>()
        );
        List<ItemDto> items = findAllItems(itemRequestDtoWithItems.getId());
        if (!items.isEmpty()) {
            itemRequestDtoWithItems.setItems(items);
        }
        return itemRequestDtoWithItems;
    }

    private List<ItemDto> findAllItems(Long id) {
        return itemRepository.findAllByItemRequestId(id)
                .stream().map(item -> toItemDto(item)).collect(Collectors.toList());
    }

}
