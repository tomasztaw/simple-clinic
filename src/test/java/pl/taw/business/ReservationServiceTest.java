package pl.taw.business;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.business.dao.DoctorScheduleDAO;
import pl.taw.business.dao.ReservationDAO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    // testujemy klasę analogiczną do klasy testowej
    @InjectMocks
    private ReservationService reservationService;

    // to już zaślepki
    @Mock
    private ReservationDAO reservationDAO;
    @Mock
    private DoctorScheduleDAO doctorScheduleDAO;
    @Mock
    private DoctorService doctorService;


    @Test
    void findAllReservationsByPatient() {
        // given
        List<ReservationDTO> reservations = List.of(
                new ReservationDTO(1, 1, 1, LocalDate.of(2023, 7, 5), LocalTime.of(10, 10), true),
                new ReservationDTO(2, 2, 1, LocalDate.of(2023, 7, 5), LocalTime.of(12, 20), true),
                new ReservationDTO(3, 3, 1, LocalDate.of(2023, 7, 6), LocalTime.of(14, 40), true));
        int patientId = 1;
        // zaślepka -> jaką metodę z wywołujemy -> co mamy dostać na wyjściu
        Mockito.when(reservationDAO.findAllByPatient(patientId)).thenReturn(reservations);

        // when -> (testujemy metodę dla klasy testowej)
        List<ReservationDTO> result = reservationService.findAllReservationsByPatient(patientId);

        // then -> (porównujemy wyniki)
        Mockito.verify(reservationDAO, Mockito.times(1)).findAllByPatient(patientId); // sprawdzamy czy mock był wywoływany 1 raz
        assertEquals(3, result.size()); // sprawdzamy wielkość listy
        assertEquals(1, result.get(0).getReservationId()); // sprawdzamy pozycje na liście
        assertEquals(2, result.get(1).getReservationId());
    }

    @Test
    void findAllReservationsByDoctor() {
        // given
        List<ReservationDTO> reservations = List.of(
                new ReservationDTO(1, 2, 1, LocalDate.of(2023, 7, 5), LocalTime.of(10, 10), true),
                new ReservationDTO(2, 2, 2, LocalDate.of(2023, 7, 5), LocalTime.of(12, 20), true),
                new ReservationDTO(3, 2, 3, LocalDate.of(2023, 7, 6), LocalTime.of(14, 40), true));
        int doctorId = 2;
        Mockito.when(reservationDAO.findAllByDoctor(doctorId)).thenReturn(reservations);

        // when
        List<ReservationDTO> result = reservationService.findAllReservationsByDoctor(doctorId);

        // then
        Mockito.verify(reservationDAO, Mockito.times(1)).findAllByDoctor(doctorId);
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getReservationId());
        assertEquals(3, result.get(result.size() - 1).getReservationId());
        assertTrue(result.stream().allMatch(res -> res.getDoctorId() == doctorId));
        Assertions.assertThat(result).hasSize(3);
    }

    @Test
    void findAllReservationsForBoth() {
        // given
        List<ReservationDTO> reservations = List.of(
                new ReservationDTO(1, 2, 3, LocalDate.of(2023, 7, 4), LocalTime.of(10, 10), true),
                new ReservationDTO(10, 2, 3, LocalDate.of(2023, 7, 5), LocalTime.of(12, 20), true),
                new ReservationDTO(30, 2, 3, LocalDate.of(2023, 7, 6), LocalTime.of(14, 40), true));
        int doctorId = 2;
        int patientId = 3;
        Mockito.when(reservationDAO.findAllByBoth(doctorId, patientId)).thenReturn(reservations);

        // when
        List<ReservationDTO> result = reservationService.findAllReservationsForBoth(doctorId, patientId);

        // then
        assertEquals(3, result.size());
        assertEquals(2, result.get(0).getDoctorId());
        assertEquals(3, result.get(1).getPatientId());
        Mockito.verify(reservationDAO, Mockito.times(1)).findAllByBoth(doctorId, patientId);
    }

    @Test
    void findAllByDay() {
        // given
        List<ReservationDTO> reservations = List.of(
                new ReservationDTO(1, 1, 1, LocalDate.of(2023, 7, 5), LocalTime.of(10, 10), true),
                new ReservationDTO(2, 2, 2, LocalDate.of(2023, 7, 5), LocalTime.of(12, 20), true),
                new ReservationDTO(5, 3, 5, LocalDate.of(2023, 7, 5), LocalTime.of(12, 20), true),
                new ReservationDTO(7, 8, 10, LocalDate.of(2023, 7, 5), LocalTime.of(14, 40), true));
        LocalDate date = LocalDate.of(2023, 7, 5);
        Mockito.when(reservationDAO.findAllByDay(date)).thenReturn(reservations);

        // when
        List<ReservationDTO> result = reservationService.findAllByDay(date);

        // then
        Mockito.verify(reservationDAO, Mockito.times(1)).findAllByDay(date);
        assertEquals(4, result.size());
        assertEquals(date, result.get(0).getDay());
        assertTrue(result.stream().allMatch(res -> res.getDay().equals(date)));
    }

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
    void isDoctorWorkingToDay() {
    }

    @Test
    void getNextAvailableDay() {
    }

    @Test
    void getNearestDayOfDoctorWorking() {
    }

    @Test
    void createReservation() {
    }

    @Test
    void cancelReservation() {
    }

    @Test
    void getAvailableTimes() {
    }
}