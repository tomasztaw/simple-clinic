package pl.taw.api.controller.rest;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleException(IllegalStateException exception) {
        return ResponseEntity
                .badRequest()
                .body(exception.getMessage());
//                .body("Coś poszło nie tak, proszę o kontakt z adminem!");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleException() {
        return ResponseEntity
                .notFound()
                .build();
    }

    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<?> handleException(ObjectNotValidException exception) {
        return ResponseEntity
                .badRequest()
                .body(exception.getErrorMessages());
    }
}
