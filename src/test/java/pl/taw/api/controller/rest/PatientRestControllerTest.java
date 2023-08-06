package pl.taw.api.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.PatientsDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.api.dto.VisitsDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientRestControllerTest {

    @Mock
    private PatientDAO patientDAO;

    @Mock
    private VisitService visitService;

    @InjectMocks
    private PatientRestController patientRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getPatientsShouldWorksCorrectly() {
        // given
        PatientsDTO patients = PatientsDTO.of(DtoFixtures.patients);
        when(patientDAO.findAll()).thenReturn(patients.getPatients());

        // when
        PatientsDTO result = patientRestController.getPatients();

        // then
        assertThat(result, is(notNullValue()));
        assertThat(result.getPatients(), hasSize(patients.getPatients().size()));
        assertThat(result.getPatients(), containsInAnyOrder(patients.getPatients().toArray()));
        verify(patientDAO, times(1)).findAll();
    }

    @Test
    void testShowPatientById_ExistingPatient() {
        // given
        Integer patientId = 1;
        PatientDTO patient = DtoFixtures.somePatient1();
        when(patientDAO.findById(patientId)).thenReturn(patient);

        // when
        ResponseEntity<PatientDTO> response = patientRestController.showPatientById(patientId);

        // then
        assertSame(patient, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testShowPatientById_NonExistingPatient() {
        // given
        Integer patientId = 1;
        when(patientDAO.findById(patientId)).thenReturn(null);

        // when
        ResponseEntity<PatientDTO> response = patientRestController.showPatientById(patientId);

        // then
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testShowHistory_ExistingPatientAndVisits() {
        // given
        Integer patientId = 2;
        PatientDTO patient = DtoFixtures.somePatient2();
        when(patientDAO.findById(patientId)).thenReturn(patient);

        VisitsDTO visitsList = VisitsDTO.of(DtoFixtures.visits);
        when(visitService.findAllVisitByPatient(patientId)).thenReturn(visitsList.getVisits());

        // when
        ResponseEntity<VisitsDTO> response = patientRestController.showHistory(patientId);

        // then
        assertSame(visitsList.getVisits(), Objects.requireNonNull(response.getBody()).getVisits());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testShowHistory_NonExistingPatient() {
        // given
        Integer patientId = 1;
        when(patientDAO.findById(patientId)).thenReturn(null);

        // when
        ResponseEntity<VisitsDTO> response = patientRestController.showHistory(patientId);

        // then
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testShowHistory_ExistingPatientNoVisits() {
        // given
        Integer patientId = 1;
        when(patientDAO.findById(patientId)).thenReturn(null);
        when(visitService.findAllVisitByPatient(patientId)).thenReturn(null);

        // when
        ResponseEntity<VisitsDTO> response = patientRestController.showHistory(patientId);

        // then
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAddPatientShouldWorksCorrectly() {
        // given
        String name = "Filemon";
        String surname = "Kowalski";
        String pesel = "90121345678";
        String phone = "+48 123 456 000";
        String email = "filip@kowalski.com";

        PatientEntity expectedPatientEntity = PatientEntity.builder()
                .name(name)
                .surname(surname)
                .pesel(pesel)
                .phone(phone)
                .email(email)
                .build();

        // when
        ResponseEntity<String> response = patientRestController.addPatient(name, surname, pesel, phone, email);

        // then
        assertEquals("Dodano pacjenta: " + name + " " + surname, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(patientDAO, times(1)).save(expectedPatientEntity);
    }

    @Test
    void testUpdatePatient_ExistingPatient() {
        // given
        Integer patientId = 5;
        String name = "Janusz";
        String surname = "Wieczorkiewicz";
        String pesel = "77013112358";
        String phone = "+48 444 888 789";
        String email = "janusz@yaho.com";

        PatientEntity existingPatient = EntityFixtures.somePatient5();
        when(patientDAO.findEntityById(patientId)).thenReturn(existingPatient);

        // when
        ResponseEntity<String> response = patientRestController.updatePatient(patientId, name, surname, pesel, phone, email);

        // then
        assertEquals("Aktualizacja wykonana", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(patientDAO, times(1)).save(existingPatient);
    }

    @Test
    void testUpdatePatient_NonExistingPatient() {
        // given
        Integer patientId = 1;
        when(patientDAO.findEntityById(patientId)).thenReturn(null);

        // when
        ResponseEntity<String> response = patientRestController.updatePatient(
                patientId, "Adam", "Kot", "66122014725", "+48 900 900 900", "adam.kot@wp.pl");

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(patientDAO, never()).save(ArgumentMatchers.any(PatientEntity.class));
    }

    @Test
    void testDeletePatient_ExistingPatient() {
        // given
        Integer patientId = 4;
        PatientEntity existingPatient = EntityFixtures.somePatient4();
        when(patientDAO.findEntityById(patientId)).thenReturn(existingPatient);

        // when
        ResponseEntity<?> response = patientRestController.deletePatient(patientId);

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(patientDAO, times(1)).delete(existingPatient);
    }

    @Test
    void testDeletePatient_NonExistingPatient() {
        // given
        Integer patientId = 1;
        when(patientDAO.findEntityById(patientId)).thenReturn(null);

        // when
        ResponseEntity<?> response = patientRestController.deletePatient(patientId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(patientDAO, never()).delete(ArgumentMatchers.any(PatientEntity.class));
    }

    @Test
    void testUpdatePatientPhone_ExistingPatient() {
        // given
        Integer patientId = 1;
        String newPhone = "+48 555 123 666";
        PatientEntity existingPatient = EntityFixtures.somePatient1();
        String expectedAnswer = String.format("Numer zaktualizowany na [%s] dla pacjenta [%s %s]", newPhone, existingPatient.getName(), existingPatient.getSurname());

        when(patientDAO.findEntityById(patientId)).thenReturn(existingPatient);

        // when
        ResponseEntity<String> response = patientRestController.updatePatientPhone(patientId, newPhone);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAnswer, response.getBody());
        verify(patientDAO, times(1)).save(existingPatient);
    }

    @Test
    void testUpdatePatientPhone_NonExistingPatient() {
        // given
        Integer patientId = -12;
        String newPhone = "+48 555 123 777";
        when(patientDAO.findEntityById(patientId)).thenReturn(null);

        // when
        ResponseEntity<String> response = patientRestController.updatePatientPhone(patientId, newPhone);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(patientDAO, never()).save(ArgumentMatchers.any(PatientEntity.class));
    }

    @Test
    void testUpdatePatientEmail_ExistingPatient() {
        // given
        Integer patientId = 2;
        String newEmail = "nowy.email@example.com";
        PatientEntity existingPatient = EntityFixtures.somePatient2();
        String expectedAnswer = String.format("Email zaktualizowany na [%s] dla pacjenta [%s %s]",
                newEmail, existingPatient.getName(), existingPatient.getSurname());

        when(patientDAO.findEntityById(patientId)).thenReturn(existingPatient);

        // when
        ResponseEntity<String> response = patientRestController.updatePatientEmail(patientId, newEmail);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAnswer, response.getBody());
        verify(patientDAO, times(1)).save(existingPatient);
    }

    @Test
    void testUpdatePatientEmail_NonExistingPatient() {
        // given
        Integer patientId = 1;
        String newEmail = "new.email@example.com";
        when(patientDAO.findEntityById(patientId)).thenReturn(null);

        // when
        ResponseEntity<String> response = patientRestController.updatePatientEmail(patientId, newEmail);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(patientDAO, never()).save(ArgumentMatchers.any(PatientEntity.class));
    }

}