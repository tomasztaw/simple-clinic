package pl.taw.business;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEntry {

    // TODO - ta klasa będzie do usunięcia po posprzątaniu

    private String dayOfWeek;
    private LocalDate date;
    private List<WorkingHours> workingHours;

}
