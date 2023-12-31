package pl.taw.business;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.business.dao.ReservationDAO;
import pl.taw.util.WorkingHoursFixtures;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceMockitoTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationDAO reservationDAO;

    @Test
    void findAllReservationsByPatient() {
        // given
        int patientId = 1;
        List<ReservationDTO> reservations = List.of(
                new ReservationDTO(1, 1, 1, LocalDate.of(2023, 7, 5), LocalTime.of(10, 10), true),
                new ReservationDTO(2, 2, 1, LocalDate.of(2023, 7, 5), LocalTime.of(12, 20), true),
                new ReservationDTO(3, 3, 1, LocalDate.of(2023, 7, 6), LocalTime.of(14, 40), true));

        when(reservationDAO.findAllByPatient(patientId)).thenReturn(reservations);

        // when
        List<ReservationDTO> result = reservationService.findAllReservationsByPatient(patientId);

        // then
        assertEquals(reservations.size(), result.size());
        assertEquals(1, result.get(0).getReservationId());
        assertEquals(2, result.get(1).getReservationId());
        assertTrue(result.stream().allMatch(res -> res.getPatientId() == patientId));
        Assertions.assertThat(result).hasSize(3);

        verify(reservationDAO, times(1)).findAllByPatient(patientId);
        verify(reservationDAO, only()).findAllByPatient(patientId);
    }

    @Test
    void findAllReservationsByDoctor() {
        // given
        int doctorId = 2;
        List<ReservationDTO> reservations = List.of(
                new ReservationDTO(1, 2, 1, LocalDate.of(2023, 7, 5), LocalTime.of(10, 10), true),
                new ReservationDTO(2, 2, 2, LocalDate.of(2023, 7, 5), LocalTime.of(12, 20), true),
                new ReservationDTO(3, 2, 3, LocalDate.of(2023, 7, 6), LocalTime.of(14, 40), true));

        when(reservationDAO.findAllByDoctor(doctorId)).thenReturn(reservations);

        // when
        List<ReservationDTO> result = reservationService.findAllReservationsByDoctor(doctorId);

        // then
        assertEquals(reservations.size(), result.size());
        assertEquals(1, result.get(0).getReservationId());
        assertEquals(3, result.get(result.size() - 1).getReservationId());
        assertTrue(result.stream().allMatch(res -> res.getDoctorId() == doctorId));
        Assertions.assertThat(result).hasSize(3);

        verify(reservationDAO, times(1)).findAllByDoctor(doctorId);
        verify(reservationDAO, only()).findAllByDoctor(doctorId);
    }

    @Test
    void findAllReservationsForBoth() {
        // given
        int doctorId = 2;
        int patientId = 3;
        List<ReservationDTO> reservations = List.of(
                new ReservationDTO(1, doctorId, patientId, LocalDate.of(2023, 7, 4), LocalTime.of(10, 10), true),
                new ReservationDTO(10, doctorId, patientId, LocalDate.of(2023, 7, 5), LocalTime.of(12, 20), true),
                new ReservationDTO(30, doctorId, patientId, LocalDate.of(2023, 7, 6), LocalTime.of(14, 40), true));

        when(reservationDAO.findAllByBoth(doctorId, patientId)).thenReturn(reservations);

        // when
        List<ReservationDTO> result = reservationService.findAllReservationsForBoth(doctorId, patientId);

        // then
        assertEquals(reservations.size(), result.size());
        assertEquals(2, result.get(0).getDoctorId());
        assertEquals(3, result.get(1).getPatientId());
        assertTrue(result.stream().allMatch(res -> res.getDoctorId() == doctorId && res.getPatientId() == patientId));
        Assertions.assertThat(result).hasSize(3);

        verify(reservationDAO, times(1)).findAllByBoth(doctorId, patientId);
        verify(reservationDAO, only()).findAllByBoth(doctorId, patientId);
    }

    @Test
    void findAllByDay() {
        // given
        LocalDate date = LocalDate.of(2023, 7, 5);
        List<ReservationDTO> reservations = List.of(
                new ReservationDTO(1, 1, 1, date, LocalTime.of(10, 10), true),
                new ReservationDTO(2, 2, 2, date, LocalTime.of(12, 20), true),
                new ReservationDTO(5, 3, 5, date, LocalTime.of(12, 20), true),
                new ReservationDTO(7, 8, 10, date, LocalTime.of(14, 40), true));

        when(reservationDAO.findAllByDay(date)).thenReturn(reservations);

        // when
        List<ReservationDTO> result = reservationService.findAllByDay(date);

        // then
        assertEquals(reservations.size(), result.size());
        assertEquals(date, result.get(0).getDay());
        assertTrue(result.stream().allMatch(res -> res.getDay().equals(date)));
        Assertions.assertThat(result).hasSize(4);

        verify(reservationDAO, times(1)).findAllByDay(date);
        verify(reservationDAO, only()).findAllByDay(date);
    }

    // TODO do zrobienia pozostałem testy
    @Test
    void simpleMapForNextWeek() {
    }

    @Test
    void simpleMap() {
    }

    @Test
    void getWorkingHoursForCurrentWeek() {
    }

    @Test
    public void testLeftDaysForThursday() {
        // given
        LocalDate today = LocalDate.of(2023, 9, 21); // czwartek

        Map<Integer, String> expected = new LinkedHashMap<>();
        expected.put(5, "2023-09-22"); // piątek

        // when
        Map<Integer, String> result = reservationService.leftDays(today);


        // then
        assertEquals(expected, result);
    }

    @Test
    public void testLeftDaysForMonday() {
        // given
        LocalDate today = LocalDate.of(2023, 9, 18); // poniedziałek

        Map<Integer, String> expected = new LinkedHashMap<>();
        expected.put(2, "2023-09-19"); // wtorek
        expected.put(3, "2023-09-20"); // środa
        expected.put(4, "2023-09-21"); // czwartek
        expected.put(5, "2023-09-22"); // piątek

        // when
        Map<Integer, String> result = reservationService.leftDays(today);

        // then
        assertEquals(expected, result);
    }

}