package pl.taw.util;

import lombok.experimental.UtilityClass;
import pl.taw.business.WorkingHours;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class WorkingHoursFixtures {


    WorkingHours.DayOfTheWeek monday = WorkingHours.DayOfTheWeek.MONDAY;
    LocalTime mondayStartTime = LocalTime.of(10, 0);
    LocalTime mondayEndTime = LocalTime.of(12, 0);
    List<String> mondayAppointmentTimes = Arrays.asList("10:00", "10:10", "10:20", "10:30", "10:40", "10:50",
            "11:10", "11:20", "11:30", "11:40", "11:50");
    public static WorkingHours mondayHours() {
        return WorkingHours.builder()
                .dayOfTheWeek(monday)
                .startTime(mondayStartTime)
                .endTime(mondayEndTime)
                .appointmentTimes(mondayAppointmentTimes)
                .build();
    }

    WorkingHours.DayOfTheWeek thursday = WorkingHours.DayOfTheWeek.THURSDAY;
    LocalTime thursdayStartTime = LocalTime.of(9, 0);
    LocalTime thursdayEndTime = LocalTime.of(12, 0);
    List<String> thursdayAppointmentTimes = Arrays.asList("09:00", "09:10", "09:20", "09:30", "09:40", "09:50",
            "10:10", "10:20", "10:30", "10:40", "10:50", "11:10", "11:20", "11:30", "11:40", "11:50");
    public static WorkingHours thursdayHours() {
        return WorkingHours.builder()
                .dayOfTheWeek(thursday)
                .startTime(thursdayStartTime)
                .endTime(thursdayEndTime)
                .appointmentTimes(thursdayAppointmentTimes)
                .build();
    }

    WorkingHours.DayOfTheWeek friday = WorkingHours.DayOfTheWeek.FRIDAY;
    LocalTime fridayStartTime = LocalTime.of(8, 0);
    LocalTime fridayEndTime = LocalTime.of(11, 0);
    List<String> fridayAppointmentTimes = Arrays.asList("08:00", "08:10", "08:20", "08:30", "08:40", "08:50",
            "09:10", "09:20", "09:30", "09:40", "09:50", "10:10", "10:20", "10:30", "10:40", "10:50");
    public static WorkingHours fridayHours() {
        return WorkingHours.builder()
                .dayOfTheWeek(friday)
                .startTime(fridayStartTime)
                .endTime(fridayEndTime)
                .appointmentTimes(fridayAppointmentTimes)
                .build();
    }
}
