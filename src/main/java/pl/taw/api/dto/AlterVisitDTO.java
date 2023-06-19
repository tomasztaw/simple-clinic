package pl.taw.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlterVisitDTO {

    private Integer visitId;
    private DoctorDTO doctor;
    private PatientDTO patient;
    private LocalDateTime dateTime;
    private String note;
    private String status;

}