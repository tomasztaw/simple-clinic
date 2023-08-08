package pl.taw.api.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationDTOTest {

    @Test
    public void testReservationDTO() {
        // given
        Integer reservationId = 1;
        Integer doctorId = 5;
        Integer patientId = 10;
        LocalDate day = LocalDate.of(2023, 8, 10);
        LocalTime startTime = LocalTime.of(14, 30);
        Boolean occupied = true;

        // when
        ReservationDTO reservationDTO = ReservationDTO.builder()
                .reservationId(reservationId)
                .doctorId(doctorId)
                .patientId(patientId)
                .day(day)
                .startTimeR(startTime)
                .occupied(occupied)
                .build();

        // then
        assertNotNull(reservationDTO);
        assertEquals(reservationId, reservationDTO.getReservationId());
        assertEquals(doctorId, reservationDTO.getDoctorId());
        assertEquals(patientId, reservationDTO.getPatientId());
        assertEquals(day, reservationDTO.getDay());
        assertEquals(startTime, reservationDTO.getStartTimeR());
        assertEquals(occupied, reservationDTO.getOccupied());
    }

}