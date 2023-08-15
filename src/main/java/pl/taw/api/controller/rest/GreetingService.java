package pl.taw.api.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.taw.api.dto.DoctorDTO;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class GreetingService {

    private final ObjectValidator<Greeting> greetingValidator;
    private final ObjectValidator<DoctorDTO> doctorValidator;

    public String saveGreeting(Greeting greeting) {

        //        var violations = greetingValidator.validate(greeting);
//        if (!violations.isEmpty()) {
//            return String.join("\n", violations);
//        }

        greetingValidator.validate(greeting);

        final String greetingMsg =
                "Greeting message <<" +
                        greeting.getMsg() +
                        ">> from: " +
                        greeting.getFrom().toUpperCase() +
                        " to: " +
                        greeting.getTo().toUpperCase();
        return greetingMsg;
    }

    public String saveDoctor(DoctorDTO doctor) {
//        Set<String> violations = doctorValidator.validate(doctor);
//        if (!violations.isEmpty()) {
//            return String.join("\n", violations);
//        }
        doctorValidator.validate(doctor);
        return "Dane lekarza są prawidłowe";
    }

    public String throwException() {
        throw new IllegalStateException("Pojawił się jakiś wyjątek");
    }
}
