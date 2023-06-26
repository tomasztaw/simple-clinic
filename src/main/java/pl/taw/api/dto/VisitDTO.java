package pl.taw.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitDTO {

    private Integer visitId;
    private Integer doctorId;
    private DoctorDTO doctor;
    private Integer patientId;
    private PatientDTO patient;
    private LocalDateTime dateTime;
    private String note;
    private String status;

    private OpinionDTO opinion;

}