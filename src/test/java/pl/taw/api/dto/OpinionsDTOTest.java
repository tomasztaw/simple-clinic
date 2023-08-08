package pl.taw.api.dto;

import org.junit.jupiter.api.Test;
import pl.taw.util.DtoFixtures;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpinionsDTOTest {

    @Test
    public void testOpinionsDTO() {
        // given
        DoctorDTO doctorDTO = DtoFixtures.someDoctor1();
        DoctorDTO doctorDTO2 = DtoFixtures.someDoctor2();

        PatientDTO patientDTO = DtoFixtures.somePatient2();
        PatientDTO patientDTO1 = DtoFixtures.somePatient1();

        VisitDTO visitDTO = DtoFixtures.someVisit2().withDoctorId(doctorDTO.getDoctorId()).withPatientId(patientDTO.getPatientId());
        VisitDTO visitDTO1 = DtoFixtures.someVisit1().withDoctorId(doctorDTO2.getDoctorId()).withPatientId(patientDTO1.getPatientId());

        OpinionDTO opinion1 = OpinionDTO.builder()
                .opinionId(1)
                .doctorId(doctorDTO.getDoctorId())
                .patientId(patientDTO.getPatientId())
                .visitId(visitDTO.getVisitId())
                .comment("Wszystko super!")
                .createdAt(LocalDateTime.of(2023, 8, 8, 12, 30))
                .doctor(doctorDTO)
                .patient(patientDTO)
                .visit(visitDTO)
                .isMapped(true)
                .build();

        OpinionDTO opinion2 = OpinionDTO.builder()
                .opinionId(2)
                .doctorId(doctorDTO2.getDoctorId())
                .patientId(patientDTO1.getPatientId())
                .visitId(visitDTO1.getVisitId())
                .comment("Świetna robota!")
                .createdAt(LocalDateTime.of(2023, 8, 8, 12, 50))
                .doctor(doctorDTO2)
                .patient(patientDTO1)
                .visit(visitDTO1)
                .isMapped(true)
                .build();

        List<OpinionDTO> opinionsList = new ArrayList<>();
        opinionsList.add(opinion1);
        opinionsList.add(opinion2);

        // when
        OpinionsDTO opinionsDTO = OpinionsDTO.builder()
                .opinions(opinionsList)
                .build();

        // then
        assertNotNull(opinionsDTO);
        assertEquals(2, opinionsDTO.getOpinions().size());
        assertEquals(opinion1, opinionsDTO.getOpinions().get(0));
        assertEquals(opinion2, opinionsDTO.getOpinions().get(1));
    }

}