package pl.taw.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@With
@Value
@Builder
@EqualsAndHashCode(of = {"name", "surname", "title"})
@ToString(of = {"doctorId", "name", "surname", "title"})
public class Doctor {

    Integer doctorId;
    String name;
    String surname;
    String title;
    @Size(min = 7, max = 15)
    @Pattern(regexp = "^[+]\\d{2}\\s\\d{3}\\s\\d{3}\\s\\d{3}$")
    String phone;
    @Email
    String email;


    List<Visit> visits;

}
