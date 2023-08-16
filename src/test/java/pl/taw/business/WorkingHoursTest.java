package pl.taw.business;

import org.junit.jupiter.api.Test;
import pl.taw.util.WorkingHoursFixtures;

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
        WorkingHours mondayHours = WorkingHoursFixtures.mondayHours();
        WorkingHours thursdayHours = WorkingHoursFixtures.thursdayHours();
        WorkingHours fridayHours = WorkingHoursFixtures.fridayHours();

        int checkAppointmentTimesSizeForMonday
                = (mondayHours.getEndTime().getHour() - mondayHours.getStartTime().getHour()) * 5 + 1;

        // when
        List<WorkingHours> doctorWorkingHours = List.of(mondayHours, thursdayHours, fridayHours);

        // then
        assertNotNull(doctorWorkingHours);
        assertEquals(List.of(mondayHours.getDayOfTheWeek(), thursdayHours.getDayOfTheWeek(), fridayHours.getDayOfTheWeek()),
                doctorWorkingHours.stream()
                        .map(WorkingHours::getDayOfTheWeek)
                        .toList());
        assertEquals(fridayHours.getStartTime(), doctorWorkingHours.get(2).getStartTime());
        assertEquals(thursdayHours.getEndTime(), doctorWorkingHours.get(1).getEndTime());
        assertEquals(mondayHours.getAppointmentTimes(), doctorWorkingHours.get(0).getAppointmentTimes());
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