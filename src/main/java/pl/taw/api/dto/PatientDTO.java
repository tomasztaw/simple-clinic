package pl.taw.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

    private Integer patientId;
    private String name;
    private String surname;
    @Pattern(regexp = "\\d{11}", message = "Niepoprawny numer PESEL")
    private String pesel;
    @Size(min = 7, max = 15)
    @Pattern(regexp = "^[+]\\d{2}\\s\\d{3}\\s\\d{3}\\s\\d{3}$")
    private String phone;
    @Email
    private String email;
    
    private List<VisitDTO> visits;

}
