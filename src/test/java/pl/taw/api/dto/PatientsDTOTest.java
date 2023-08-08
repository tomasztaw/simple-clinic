package pl.taw.api.dto;

import org.junit.jupiter.api.Test;
import pl.taw.util.DtoFixtures;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PatientsDTOTest {

    @Test
    public void testPatientsDTO() {
        // given
        PatientDTO patient1 = DtoFixtures.somePatient1();
        PatientDTO patient2 = DtoFixtures.somePatient2();

        List<PatientDTO> patientsList = new ArrayList<>();
        patientsList.add(patient1);
        patientsList.add(patient2);

        // when
        PatientsDTO patientsDTO = PatientsDTO.builder()
                .patients(patientsList)
                .build();

        // then
        assertNotNull(patientsDTO);
        assertEquals(2, patientsDTO.getPatients().size());
        assertEquals(patient1, patientsDTO.getPatients().get(0));
        assertEquals(patient2, patientsDTO.getPatients().get(1));
    }

    @Test
    public void testEmptyPatientsDTO() {
        // given, when
        PatientsDTO patientsDTO = PatientsDTO.builder().build();

        // then
        assertNotNull(patientsDTO);
        assertNull(patientsDTO.getPatients());
    }

    @Test
    public void testWithPatients() {
        // given
        PatientDTO patientDTO = DtoFixtures.somePatient1();
        List<PatientDTO> patientsList = new ArrayList<>();
        patientsList.add(patientDTO);

        PatientsDTO originalDTO = PatientsDTO.builder().build();

        // when
        PatientsDTO updatedDTO = originalDTO.withPatients(patientsList);

        // then
        assertNotNull(updatedDTO);
        assertEquals(patientsList, updatedDTO.getPatients());
    }

    @Test
    public void testEquality() {
        // given
        PatientDTO patientDTO = DtoFixtures.somePatient2();
        List<PatientDTO> patientsList = new ArrayList<>();
        patientsList.add(patientDTO);

        // when
        PatientsDTO dto1 = PatientsDTO.builder().patients(patientsList).build();
        PatientsDTO dto2 = PatientsDTO.builder().patients(patientsList).build();

        // then
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testOfMethod() {
        // given
        PatientDTO patientDTO = DtoFixtures.somePatient1();
        List<PatientDTO> patientsList = new ArrayList<>();
        patientsList.add(patientDTO);

        // when
        PatientsDTO patientsDTO = PatientsDTO.of(patientsList);

        // then
        assertNotNull(patientsDTO);
        assertEquals(patientsList, patientsDTO.getPatients());
    }

}