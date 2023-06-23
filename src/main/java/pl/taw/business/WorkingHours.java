package pl.taw.business;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkingHours {

    private DayOfTheWeek dayOfTheWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    private List<String> appointmentTimes;
    private final int intervalMinutes = 10;
    private final int breakMinutes = 10;



    public enum DayOfTheWeek {
        MONDAY(1, "Poniedziałek"),
        TUESDAY(2, "Wtorek"),
        WEDNESDAY(3, "Środa"),
        THURSDAY(4, "Czwartek"),
        FRIDAY(5, "Piątek"),
        SATURDAY(6, "Sobota"),
        SUNDAY(7, "Niedziela");

        private final int number;
        private final String name;

        DayOfTheWeek(int number, String name) {
            this.number = number;
            this.name = name;
        }

        public int getNumber() {
            return number;
        }

        public String getName() {
            return name;
        }

        public static DayOfTheWeek fromInt(int number) {
            for (DayOfTheWeek day : DayOfTheWeek.values()) {
                if (day.getNumber() == number) {
                    return day;
                }
            }
            throw new IllegalArgumentException("Invalid day of the week number: " + number);
        }
    }

}
