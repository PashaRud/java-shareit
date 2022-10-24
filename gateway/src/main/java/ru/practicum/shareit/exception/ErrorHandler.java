package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(EmailValidateException e) {
        log.error("ошибка валидации email-а" + "\n" + e.getMessage());
        return new ErrorResponse("Email validation error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleStorageException(StorageException e) {
        log.error("Объект не найден" + "\n" + e.getMessage());
        return new ErrorResponse("NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemException(ItemException e) {
        log.error("Item не найден" + "\n" + e.getMessage());
        return new ErrorResponse("NOT_AVAILABLE", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingException(BookingException e) {
        log.error("Некорректный запрос" + "\n" + e.getMessage());
        return new ErrorResponse("incorrect request", e.getMessage());
    }
}