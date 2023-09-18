package pl.taw.domain.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
//        String status = ex.getLocalizedMessage();
        String status = ex.getMessage(); // ta metoda obsługuje błędy z username i email
        String message = "Wystąpił niespodziewany błąd: [%s]".formatted(ex.getMessage());
        log.error(message, ex);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", message);
        modelAndView.addObject("status", status);
        return modelAndView;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleException(EntityNotFoundException ex) {
        String status = String.valueOf(HttpStatus.NOT_FOUND.value());
        String message = "Nie można znaleźć zasobu: [%s]".formatted(ex.getMessage());
        log.error(message, ex);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", message);
        return modelAndView;
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleException(BindException ex) {
        String status = String.valueOf(HttpStatus.BAD_REQUEST.value());
        String message = "Niepoprawne żądanie dla pola: [%s] niewłaściwa wartość: [%s]".formatted(
                Optional.ofNullable(ex.getFieldError()).map(FieldError::getField).orElse(null),
                Optional.ofNullable(ex.getFieldError()).map(FieldError::getRejectedValue).orElse(null)
        );
        String hint = "Brak sugestii";
        if ("phone".equals(ex.getFieldError().getField())) {
            hint = "Numer powinien być w formacie [+48 xxx xxx xxx]";
        } else if ("email".equals(ex.getFieldError().getField())) {
            hint = "Email powinien być w formacie [adamkowalski@poczta.pl]";
        }
        log.error(message, ex);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", message);
        modelAndView.addObject("status", status);
        modelAndView.addObject("hint", hint);
        return modelAndView;
    }
}
