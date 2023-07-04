package pl.taw.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.DoctorScheduleDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.DoctorScheduleDAO;
import pl.taw.util.DtoFixtures;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @InjectMocks
    private DoctorService doctorService;

    @Mock
    private DoctorDAO doctorDAO;

    @Mock
    private DoctorScheduleDAO doctorScheduleDAO;


    @Test
    void getDoctorsBySpecialization() {
        // given
        String specialization = "Lekarz rodzinny";
        List<DoctorDTO> doctors = List.of(
                DtoFixtures.someDoctor1().withTitle(specialization),
                DtoFixtures.someDoctor2().withTitle(specialization),
                DtoFixtures.someDoctor3().withTitle(specialization));
        Mockito.when(doctorDAO.findBySpecialization(specialization)).thenReturn(doctors);

        // when
        List<DoctorDTO> result = doctorService.getDoctorsBySpecialization(specialization);

        // then
        Mockito.verify(doctorDAO, Mockito.times(1)).findBySpecialization(specialization);
        assertTrue(result.stream().allMatch(doctor -> doctor.getTitle().equals(specialization)));
        assertEquals(3, result.size());
    }

    @Test
    void getDoctorSpecializations() {
        // given
        List<String> specializations = List.of("Lekarz rodzinny", "Kardiolog", "Psychiatra");
        Mockito.when(doctorDAO.findAllSpecializations()).thenReturn(specializations);

        // when
        List<String> result = doctorService.getDoctorsSpecializations();

        // then
        Mockito.verify(doctorDAO, Mockito.times(1)).findAllSpecializations();
        assertEquals(3, result.size());
        assertSame(specializations, result); // to jest fajne porównanie, do zapamiętania
    }

//    @Test
//    void getWorkingHours() {
//    }

    @Test
    void getWorkingHours() {
        // given
        Integer doctorId = 1;
        DoctorScheduleDTO schedule1 = DoctorScheduleDTO.builder()
                .scheduleId(1)
                .doctorId(doctorId)
                .dayOfTheWeek(1)
                .startTimeDs(LocalTime.of(10, 0))
                .endTimeDs(LocalTime.of(14, 0))
                .build();
        DoctorScheduleDTO schedule2 = DoctorScheduleDTO.builder()
                .scheduleId(2)
                .doctorId(doctorId)
                .dayOfTheWeek(2)
                .startTimeDs(LocalTime.of(8, 0))
                .endTimeDs(LocalTime.of(11, 0))
                .build();

        List<DoctorScheduleDTO> doctorSchedules = Arrays.asList(schedule1, schedule2);

        Mockito.when(doctorScheduleDAO.findScheduleByDoctorId(doctorId)).thenReturn(doctorSchedules);

        // when
        List<WorkingHours> result = doctorService.getWorkingHours(doctorId);

        // then
        Mockito.verify(doctorScheduleDAO, Mockito.times(1)).findScheduleByDoctorId(doctorId);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getDayOfTheWeek().getNumber());
        assertEquals(2, result.get(1).getDayOfTheWeek().getNumber());
        assertEquals(LocalTime.of(10, 0), result.get(0).getStartTime());


        // Test convertToWorkingHoursList -> (pamiętaj o przerwie co pełnej godzinie)
        WorkingHours workingHours1 = result.get(0);
        assertEquals(WorkingHours.DayOfTheWeek.MONDAY, workingHours1.getDayOfTheWeek());
        assertEquals(LocalTime.of(10, 0), workingHours1.getStartTime());
        assertEquals(LocalTime.of(14, 0), workingHours1.getEndTime());
        assertEquals(10, workingHours1.getIntervalMinutes());
        assertEquals(Arrays.asList("10:00", "10:10", "10:20", "10:30",
                "10:40", "10:50", "11:10", "11:20", "11:30", "11:40", "11:50",
                "12:10", "12:20", "12:30", "12:40", "12:50", "13:10",
                "13:20", "13:30", "13:40", "13:50"), workingHours1.getAppointmentTimes());

        WorkingHours workingHours2 = result.get(1);
        assertEquals(WorkingHours.DayOfTheWeek.TUESDAY, workingHours2.getDayOfTheWeek());
        assertEquals(LocalTime.of(8, 0), workingHours2.getStartTime());
        assertEquals(LocalTime.of(11, 0), workingHours2.getEndTime());
        assertEquals(10, workingHours2.getIntervalMinutes());
        assertEquals(Arrays.asList("08:00", "08:10", "08:20", "08:30", "08:40", "08:50",
                "09:10", "09:20", "09:30", "09:40", "09:50", "10:10", "10:20", "10:30",
                "10:40", "10:50"), workingHours2.getAppointmentTimes());

    }

}