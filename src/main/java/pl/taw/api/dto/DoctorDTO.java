package pl.taw.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {

    private Integer doctorId;
    private String name;
    private String surname;
    private String title;
    @Size(min = 7, max = 15)
    @Pattern(regexp = "^[+]\\d{2}\\s\\d{3}\\s\\d{3}\\s\\d{3}$")
    private String phone;
    @Email
    private String email;

    private List<VisitDTO> visits;

    public Map<String, String> asMap() {
        Map<String, String> result = new HashMap<>();
        Optional.ofNullable(doctorId).ifPresent(value -> result.put("doctorId", value.toString()));
        Optional.ofNullable(name).ifPresent(value -> result.put("name", value));
        Optional.ofNullable(surname).ifPresent(value -> result.put("surname", value));
        Optional.ofNullable(title).ifPresent(value -> result.put("title", value));
        Optional.ofNullable(phone).ifPresent(value -> result.put("phone", value));
        Optional.ofNullable(email).ifPresent(value -> result.put("email", value));
        return result;
    }

}
