package ru.make.account.core.arving.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class HandlerException extends ResponseEntityExceptionHandler {
    @ExceptionHandler({ProcessException.class})
    public ResponseEntity<?> handleProcessException(ProcessException exception) {
        log.error("Ошибка выполнения: {}", exception.getClass().getName());
        return new ResponseEntity<>(
                Map.of("timestamp", LocalDateTime.now(),
                        "error", exception.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<?> handleAllUncaughtException(RuntimeException exception,
                                                        WebRequest request) {
        log.error("Ошибка выполнения: {}", exception.getClass().getName());
        log.error("Ошибка: {}", exception.getMessage());
        exception.printStackTrace();
        return new ResponseEntity<>(
                Map.of("timestamp", LocalDateTime.now(),
                        "error", "Непредвиденная ошибка"),
                HttpStatus.BAD_REQUEST);
    }
}
