package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.model.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoWithBooking> findAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findAll(userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту /items. Вещь: " +  itemDto.getName()
                 + "Наименование: " + itemDto.getDescription());
        return itemService.save(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id,
                          @RequestBody ItemDto itemDto) {
        log.info("Обновление user id = " + userId + ". Вещь: " + id);
        return itemService.update(itemDto, userId, id);
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Valid @RequestBody CommentDto commentDto,
                                    @PathVariable long itemId) {
        log.info("Получен запрос к эндпоинту: /items/{itemId}/comment. Вещь: " + itemId +
                "Комментарий: " + commentDto.getText());
        return itemService.saveComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking findItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Полечение вещи " + itemId);
        return itemService.findById(itemId, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItemById(@PathVariable long id) {
        log.info("Удаление вещи " +id);
        itemService.deleteById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemByText(@RequestParam String text) {
        log.info("Поиск вещи по тексту - " + text);
        return itemService.searchItem(text);
    }
}