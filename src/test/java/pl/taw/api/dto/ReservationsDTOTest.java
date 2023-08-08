package pl.taw.api.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationsDTOTest {

    @Test
    public void testReservationsDTO() {
        // given
        LocalDate day1 = LocalDate.of(2023, 8, 10);
        LocalTime startTime1 = LocalTime.of(14, 30);
        Boolean occupied1 = true;

        LocalDate day2 = LocalDate.of(2023, 8, 11);
        LocalTime startTime2 = LocalTime.of(16, 0);
        Boolean occupied2 = true;

        ReservationDTO reservation1 = ReservationDTO.builder()
                .day(day1)
                .startTimeR(startTime1)
                .occupied(occupied1)
                .build();

        ReservationDTO reservation2 = ReservationDTO.builder()
                .day(day2)
                .startTimeR(startTime2)
                .occupied(occupied2)
                .build();

        List<ReservationDTO> reservationsList = new ArrayList<>();
        reservationsList.add(reservation1);
        reservationsList.add(reservation2);

        // when
        ReservationsDTO reservationsDTO = ReservationsDTO.builder()
                .reservations(reservationsList)
                .build();

        // then
        assertNotNull(reservationsDTO);
        assertEquals(2, reservationsDTO.getReservations().size());
        assertEquals(reservation1, reservationsDTO.getReservations().get(0));
        assertEquals(reservation2, reservationsDTO.getReservations().get(1));
    }

}