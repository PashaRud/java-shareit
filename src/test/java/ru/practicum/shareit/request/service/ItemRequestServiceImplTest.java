package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.StorageException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {

    private ItemRequestRepository itemRequestRepository;
    private ItemRequestMapper itemRequestMapper;
    private UserRepository userRepository;
    private ItemRequestService itemRequestService;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        ItemRepository itemRepository = mock(ItemRepository.class);
        ItemMapper itemMapper = mock(ItemMapper.class);
        itemRequestMapper = new ItemRequestMapper(itemRepository);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository,
                itemRequestMapper, userRepository);
        itemRequest = createItemRequest();
    }

    private ItemRequest createItemRequest() {
        User user2 = new User(2L, "user2", "user2@mail.ru");
        return new ItemRequest(1L, "itemRequest1", LocalDateTime.now(), user2);
    }

    @Test
    void saveItemRequestTest() {
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        when(userRepository.findById(itemRequest.getUser().getId()))
                .thenReturn(Optional.of(itemRequest.getUser()));
        ItemRequestDto itemRequestDto = itemRequestService
                .save(itemRequestMapper.toItemRequestDto(itemRequest),
                        itemRequest.getUser().getId());
        assertNotNull(itemRequestDto);
        assertEquals("itemRequest1", itemRequestDto.getDescription());
        assertEquals("user2", itemRequest.getUser().getName());
        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void findAllRequestsTest() {
        when(userRepository.findById(itemRequest.getUser().getId()))
                .thenReturn(Optional.of(itemRequest.getUser()));
        when(itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(itemRequest.getUser().getId()))
                .thenReturn(Collections.singletonList(itemRequest));
        final List<ItemRequestDtoWithItems> itemRequestDtoWithItems = itemRequestService
                .findAll(itemRequest.getUser().getId());
        assertNotNull(itemRequestDtoWithItems);
        assertEquals(0, itemRequestDtoWithItems.size());
        assertEquals(itemRequest.getDescription(), itemRequestDtoWithItems.get(0).getDescription());
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdOrderByCreatedDesc(itemRequest.getUser().getId());
    }

    @Test
    void findRequestByIdTest() {
        Long itemRequestId = itemRequest.getId();
        when(userRepository.findById(itemRequest.getUser().getId()))
                .thenReturn(Optional.of(itemRequest.getUser()));
        long incorrectId = (long) (Math.random() * 100) + itemRequestId + 3;
        when(itemRequestRepository.findById(itemRequestId))
                .thenReturn(Optional.of(itemRequest));
        when(itemRequestRepository.findById(incorrectId))
                .thenThrow(new StorageException("Запроса с Id = " + incorrectId + " нет в БД"));
        ItemRequestDtoWithItems itemRequestDtoWithItems = itemRequestService
                .findByRequestId(itemRequest.getUser().getId(), itemRequestId);
        assertNotNull(itemRequestDtoWithItems);
        assertEquals("itemRequest1", itemRequestDtoWithItems.getDescription());
        Throwable thrown = assertThrows(StorageException.class,
                () -> itemRequestService.findByRequestId(itemRequest.getUser().getId(), incorrectId));
        assertNotNull(thrown.getMessage());
        verify(itemRequestRepository, times(1)).findById(itemRequestId);
    }

    @Test
    void findAllWithPageableRequestsTest() {
        when(userRepository.findById(itemRequest.getUser().getId()))
                .thenReturn(Optional.of(itemRequest.getUser()));
        when(itemRequestRepository
                .findAll(PageRequest.of(0, 20, Sort.by("created"))))
                .thenReturn(Page.empty());
        final List<ItemRequestDtoWithItems> itemRequestDtoWithItems = itemRequestService
                .findAllRequests(itemRequest.getUser().getId(), 0, 20);
        assertNotNull(itemRequestDtoWithItems);
        assertTrue(itemRequestDtoWithItems.isEmpty());
        verify(itemRequestRepository, times(1))
                .findAll(PageRequest.of(0, 20, Sort.by("created")));
    }
}
