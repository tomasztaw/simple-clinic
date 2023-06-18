package pl.taw.domain;

import lombok.*;

import java.time.LocalDateTime;

@With
@Value
@Builder
@EqualsAndHashCode(of = {"visitId", "note", "status"})
@ToString(of = {"visitId", "doctor", "patient", "dateTime", "note", "status"})
public class Visit {

    Integer visitId;
    Doctor doctor;
    Patient patient;
    LocalDateTime dateTime;
    String note;
    String status;


}
