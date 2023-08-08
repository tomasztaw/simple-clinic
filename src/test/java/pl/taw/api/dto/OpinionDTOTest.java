package pl.taw.api.dto;

import org.junit.jupiter.api.Test;
import pl.taw.util.DtoFixtures;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OpinionDTOTest {

    @Test
    public void testOpinionDTO() {
        // given
        Integer opinionId = 1;
        Integer doctorId = 3;
        Integer patientId = 2;
        Integer visitId = 4;
        String comment = "Super lekarz!";
        LocalDateTime createdAt = LocalDateTime.of(2023, 8, 8, 12, 20);

        DoctorDTO doctorDTO = DtoFixtures.someDoctor3();
        PatientDTO patientDTO = DtoFixtures.somePatient2();
        VisitDTO visitDTO = DtoFixtures.someVisit4();

        // when
        OpinionDTO opinionDTO = OpinionDTO.builder()
                .opinionId(opinionId)
                .doctorId(doctorId)
                .patientId(patientId)
                .visitId(visitId)
                .comment(comment)
                .createdAt(createdAt)
                .doctor(doctorDTO)
                .patient(patientDTO)
                .visit(visitDTO)
                .isMapped(true)
                .build();

        // then
        assertNotNull(opinionDTO);
        assertEquals(opinionId, opinionDTO.getOpinionId());
        assertEquals(doctorId, opinionDTO.getDoctorId());
        assertEquals(patientId, opinionDTO.getPatientId());
        assertEquals(visitId, opinionDTO.getVisitId());
        assertEquals(comment, opinionDTO.getComment());
        assertEquals(createdAt, opinionDTO.getCreatedAt());
        assertEquals(doctorDTO, opinionDTO.getDoctor());
        assertEquals(patientDTO, opinionDTO.getPatient());
        assertEquals(visitDTO, opinionDTO.getVisit());
        assertTrue(opinionDTO.isMapped());
    }

}