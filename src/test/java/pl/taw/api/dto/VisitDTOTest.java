package pl.taw.api.dto;

import org.junit.jupiter.api.Test;
import pl.taw.util.DtoFixtures;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VisitDTOTest {

    @Test
    public void testVisitDTO() {
        // given
        Integer visitId = 1;
        Integer doctorId = 3;
        Integer patientId = 1;
        LocalDateTime dateTime = LocalDateTime.of(2023, 8, 10, 14, 30);
        String note = "Wizyta kontrolna, regres choroby";
        String status = "odbyta";

        DoctorDTO doctorDTO = DtoFixtures.someDoctor3();
        PatientDTO patientDTO = DtoFixtures.somePatient1();
        OpinionDTO opinionDTO = DtoFixtures.someOpinion1();

        // when
        VisitDTO visitDTO = VisitDTO.builder()
                .visitId(visitId)
                .doctorId(doctorId)
                .doctor(doctorDTO)
                .patientId(patientId)
                .patient(patientDTO)
                .dateTime(dateTime)
                .note(note)
                .status(status)
                .opinion(opinionDTO)
                .build();

        // then
        assertNotNull(visitDTO);
        assertEquals(visitId, visitDTO.getVisitId());
        assertEquals(doctorId, visitDTO.getDoctorId());
        assertEquals(doctorDTO, visitDTO.getDoctor());
        assertEquals(patientId, visitDTO.getPatientId());
        assertEquals(patientDTO, visitDTO.getPatient());
        assertEquals(dateTime, visitDTO.getDateTime());
        assertEquals(note, visitDTO.getNote());
        assertEquals(status, visitDTO.getStatus());
        assertEquals(opinionDTO, visitDTO.getOpinion());
    }

}