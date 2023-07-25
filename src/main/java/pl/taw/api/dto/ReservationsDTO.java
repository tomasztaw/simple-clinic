package pl.taw.api.dto;

import lombok.*;

import java.util.List;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ReservationsDTO {

    private List<ReservationDTO> reservations;

}
