package pl.taw.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@With
@Value
@Builder
@EqualsAndHashCode(of = "patientId")
@ToString(of = {"patientId", "name", "surname", "pesel"})
public class Patient {

    Integer patientId;
    String name;
    String surname;
    @Pattern(regexp = "\\d{11}", message = "Invalid PESEL number")
    String pesel;
    @Size(min = 7, max = 15)
    @Pattern(regexp = "^[+]\\d{2}\\s\\d{3}\\s\\d{3}\\s\\d{3}$")
    String phone;
    @Email
    String email;

    List<Visit> visits;

}
