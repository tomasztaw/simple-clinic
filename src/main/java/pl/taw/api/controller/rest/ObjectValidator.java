package pl.taw.api.controller.rest;

import jakarta.validation.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ObjectValidator<T> {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public void validate(T objectToValidate) {
        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate);
        if (!violations.isEmpty()) {
            var errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet());
            throw new ObjectNotValidException(errorMessages);
        }
    }

//    public Set<String> validate(T objectToValidate) {
//        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate);
//        if (!violations.isEmpty()) {
//            return violations.stream()
//                    .map(ConstraintViolation::getMessage)
//                    .collect(Collectors.toSet());
//        }
//        return Collections.emptySet();
//    }
}
