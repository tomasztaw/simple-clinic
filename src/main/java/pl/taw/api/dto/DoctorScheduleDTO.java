package pl.taw.api.dto;

import lombok.*;
import pl.taw.infrastructure.database.entity.DoctorEntity;

import java.time.LocalTime;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleDTO {

    private Integer scheduleId;
    private Integer doctorId;
    private Integer dayOfTheWeek;
    private LocalTime startTimeDs;
    private LocalTime endTimeDs;

    // relacje
    private DoctorEntity doctor;

}
