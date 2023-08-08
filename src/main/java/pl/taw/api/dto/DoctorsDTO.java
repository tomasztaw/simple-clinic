package pl.taw.api.dto;

import lombok.*;

import java.util.List;

/**
 * Dla sprawdzenia zwracanej listy przez rest api.
 * Zwracany jest jeden obiekt zawierający listę.
 * */
@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class DoctorsDTO {

    private List<DoctorDTO> doctors;

}
