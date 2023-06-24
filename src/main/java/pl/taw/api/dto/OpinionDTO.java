package pl.taw.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpinionDTO {

    private Integer opinionId;
    private Integer doctorId;
    private Integer patientId;
    private Integer visitId;
    private String comment;
    private LocalDateTime createdAt;

    private DoctorDTO doctor;
    private PatientDTO patient;
    private VisitDTO visit;
}
