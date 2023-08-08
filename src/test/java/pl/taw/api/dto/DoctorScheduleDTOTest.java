package pl.taw.api.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DoctorScheduleDTOTest {

    @Test
    public void testDoctorScheduleDTO() {
        // given
        Integer scheduleId = 1;
        Integer doctorId = 5;
        Integer dayOfTheWeek = 3;
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);

        // when
        DoctorScheduleDTO doctorScheduleDTO = DoctorScheduleDTO.builder()
                .scheduleId(scheduleId)
                .doctorId(doctorId)
                .dayOfTheWeek(dayOfTheWeek)
                .startTimeDs(startTime)
                .endTimeDs(endTime)
                .build();

        // then
        assertNotNull(doctorScheduleDTO);
        assertEquals(scheduleId, doctorScheduleDTO.getScheduleId());
        assertEquals(doctorId, doctorScheduleDTO.getDoctorId());
        assertEquals(dayOfTheWeek, doctorScheduleDTO.getDayOfTheWeek());
        assertEquals(startTime, doctorScheduleDTO.getStartTimeDs());
        assertEquals(endTime, doctorScheduleDTO.getEndTimeDs());
    }

}