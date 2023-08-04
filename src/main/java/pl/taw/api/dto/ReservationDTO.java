package pl.taw.api.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    private Integer reservationId;
    private Integer doctorId;
    private Integer patientId;
    private LocalDate day;
    private LocalTime startTimeR;
    private Boolean occupied;

}
