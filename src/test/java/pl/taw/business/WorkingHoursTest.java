package pl.taw.business;

import org.junit.jupiter.api.Test;
import pl.taw.api.dto.DoctorScheduleDTO;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkingHoursTest {

    @Test
    void testCreateWorkingHours() {
        // given
        WorkingHours.DayOfTheWeek dayOfTheWeek = WorkingHours.DayOfTheWeek.MONDAY;
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        List<String> appointmentTimes = Arrays.asList("09:00", "09:10", "09:20", "09:30");

        // when
        WorkingHours workingHours = WorkingHours.builder()
                .dayOfTheWeek(dayOfTheWeek)
                .startTime(startTime)
                .endTime(endTime)
                .appointmentTimes(appointmentTimes)
                .build();

        // then
        assertNotNull(workingHours);
        assertEquals(dayOfTheWeek, workingHours.getDayOfTheWeek());
        assertEquals(startTime, workingHours.getStartTime());
        assertEquals(endTime, workingHours.getEndTime());
        assertEquals(appointmentTimes, workingHours.getAppointmentTimes());
        assertEquals(10, workingHours.getIntervalMinutes());
        assertEquals(10, workingHours.getBreakMinutes());
    }

    @Test
    void testCreateFullWeekWorkingHours() {
        // given
        WorkingHours.DayOfTheWeek monday = WorkingHours.DayOfTheWeek.MONDAY;
        LocalTime mondayStartTime = LocalTime.of(10, 0);
        LocalTime mondayEndTime = LocalTime.of(12, 0);
        List<String> mondayAppointmentTimes = Arrays.asList("10:00", "10:10", "10:20", "10:30", "10:40", "10:50",
                "11:10", "11:20", "11:30", "11:40", "11:50");
        WorkingHours mondayHours = WorkingHours.builder()
                .dayOfTheWeek(monday)
                .startTime(mondayStartTime)
                .endTime(mondayEndTime)
                .appointmentTimes(mondayAppointmentTimes)
                .build();
        WorkingHours.DayOfTheWeek thursday = WorkingHours.DayOfTheWeek.THURSDAY;
        LocalTime thursdayStartTime = LocalTime.of(9, 0);
        LocalTime thursdayEndTime = LocalTime.of(12, 0);
        List<String> thursdayAppointmentTimes = Arrays.asList("09:00", "09:10", "09:20", "09:30", "09:40", "09:50",
                "10:10", "10:20", "10:30", "10:40", "10:50", "11:10", "11:20", "11:30", "11:40", "11:50");
        WorkingHours thursdayHours = WorkingHours.builder()
                .dayOfTheWeek(thursday)
                .startTime(thursdayStartTime)
                .endTime(thursdayEndTime)
                .appointmentTimes(thursdayAppointmentTimes)
                .build();
        WorkingHours.DayOfTheWeek friday = WorkingHours.DayOfTheWeek.FRIDAY;
        LocalTime fridayStartTime = LocalTime.of(8, 0);
        LocalTime fridayEndTime = LocalTime.of(11, 0);
        List<String> fridayAppointmentTimes = Arrays.asList("08:00", "08:10", "08:20", "08:30", "08:40", "08:50",
                "09:10", "09:20", "09:30", "09:40", "09:50", "10:10", "10:20", "10:30", "10:40", "10:50");
        WorkingHours fridayHours = WorkingHours.builder()
                .dayOfTheWeek(friday)
                .startTime(fridayStartTime)
                .endTime(fridayEndTime)
                .appointmentTimes(fridayAppointmentTimes)
                .build();
        int checkAppointmentTimesSizeForMonday = (mondayEndTime.getHour() - mondayStartTime.getHour()) * 5 + 1;

        // when
        List<WorkingHours> doctorWorkingHours = List.of(mondayHours, thursdayHours, fridayHours);

        // then
        assertNotNull(doctorWorkingHours);
        assertEquals(List.of(monday, thursday, friday), doctorWorkingHours.stream().map(WorkingHours::getDayOfTheWeek).toList());
        assertEquals(fridayStartTime, doctorWorkingHours.get(2).getStartTime());
        assertEquals(thursdayEndTime, doctorWorkingHours.get(1).getEndTime());
        assertEquals(mondayAppointmentTimes, doctorWorkingHours.get(0).getAppointmentTimes());
        assertEquals(checkAppointmentTimesSizeForMonday, doctorWorkingHours.get(0).getAppointmentTimes().size());

    }

    @Test
    void testDayOfTheWeekFromInt() {
        // given
        int number = 3; // Wednesday

        // when
        WorkingHours.DayOfTheWeek dayOfTheWeek = WorkingHours.DayOfTheWeek.fromInt(number);

        // then
        assertEquals(WorkingHours.DayOfTheWeek.WEDNESDAY, dayOfTheWeek);
        assertEquals("Środa", dayOfTheWeek.getName());
    }

    @Test
    void testDayOfTheWeekFromInvalidInt() {
        // given
        int invalidNumber = 8;

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            // when
            WorkingHours.DayOfTheWeek.fromInt(invalidNumber);
        });
    }

    @Test
    void testGetNumber() {
        // given
        WorkingHours.DayOfTheWeek dayOfTheWeek = WorkingHours.DayOfTheWeek.SATURDAY;

        // when
        int number = dayOfTheWeek.getNumber();

        // then
        assertEquals(6, number);
    }

    @Test
    void testGetListOfDayNumbers() {
        // given
        List<WorkingHours.DayOfTheWeek> days = Arrays.asList(
                WorkingHours.DayOfTheWeek.MONDAY,
                WorkingHours.DayOfTheWeek.WEDNESDAY,
                WorkingHours.DayOfTheWeek.FRIDAY);

        // when
        List<Integer> result = days.stream().map(WorkingHours.DayOfTheWeek::getNumber).toList();

        // then
        assertEquals(3, result.size());
        assertTrue(result.contains(1));
        assertTrue(result.contains(3));
        assertTrue(result.contains(5));
        assertEquals(List.of(1, 3, 5), result);
    }

    @Test
    void testGetName() {
        // given
        WorkingHours.DayOfTheWeek dayOfTheWeek = WorkingHours.DayOfTheWeek.SUNDAY;

        // when
        String name = dayOfTheWeek.getName();

        // then
        assertEquals("Niedziela", name);
    }

    @Test
    void testGetListWithPolishNames() {
        // given
        List<WorkingHours.DayOfTheWeek> days = Arrays.asList(
                WorkingHours.DayOfTheWeek.MONDAY,
                WorkingHours.DayOfTheWeek.TUESDAY,
                WorkingHours.DayOfTheWeek.WEDNESDAY,
                WorkingHours.DayOfTheWeek.THURSDAY,
                WorkingHours.DayOfTheWeek.FRIDAY);
        List<String> expectedList = List.of("Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek");

        // when
        List<String> result = days.stream()
                .map(WorkingHours.DayOfTheWeek::getName)
                .toList();

        // then
        assertEquals(5, result.size());
        assertEquals(expectedList, result);
    }

}