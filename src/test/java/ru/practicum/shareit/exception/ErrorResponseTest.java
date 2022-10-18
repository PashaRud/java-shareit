package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorResponseTest {

    private ErrorResponse errorResponse = new ErrorResponse("message", "error");

    @Test
    void getError() {
        assertEquals("error", errorResponse.getError());
    }

    @Test
    void getDescription() {
        assertEquals("message", errorResponse.getMessage());
    }
}
