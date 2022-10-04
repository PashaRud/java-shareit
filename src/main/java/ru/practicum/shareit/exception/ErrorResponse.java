package ru.practicum.shareit.exception;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Getter
public class ErrorResponse {

    private String message;
    private String error;

    public ErrorResponse(String message, String error) {
        this.message = message;
        this.error = error;
    }
}
