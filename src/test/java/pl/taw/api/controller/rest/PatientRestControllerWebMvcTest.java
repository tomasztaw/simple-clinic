package pl.taw.api.controller.rest;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.PatientsDTO;
import pl.taw.api.dto.VisitsDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.taw.api.controller.rest.PatientRestController.*;

@WebMvcTest(controllers = PatientRestController.class)
@AutoConfigureMockMvc(addFilters = false) // wyłączenie filtrów security dla testów Mock Mvc
@AllArgsConstructor(onConstructor = @__(@Autowired))
class PatientRestControllerWebMvcTest {

    @MockBean
    private PatientDAO patientDAO;

    @MockBean
    private VisitService visitService;

    private MockMvc mockMvc;

    @Test
    void getPatientsShouldWorksCorrectly() throws Exception {
        // given
        PatientsDTO patients = PatientsDTO.of(DtoFixtures.patients);

        when(patientDAO.findAll()).thenReturn(patients.getPatients());

        // when, then
        mockMvc.perform(get(API_PATIENTS))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", notNullValue()))
                .andExpect(jsonPath("$.patients", hasSize(patients.getPatients().size())));

        verify(patientDAO, times(1)).findAll();
        verify(patientDAO, only()).findAll();
    }

    @Test
    void testShowPatientById_ExistingPatient() throws Exception {
        // given
        Integer patientId = 1;
        PatientDTO patient = DtoFixtures.somePatient1();

        when(patientDAO.findById(patientId)).thenReturn(patient);

        // when, then
        mockMvc.perform(get(API_PATIENTS.concat(PATIENT_ID), patientId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId", is(patient.getPatientId())))
                .andExpect(jsonPath("$.name", is(patient.getName())))
                .andExpect(jsonPath("$.surname", is(patient.getSurname())))
                .andExpect(jsonPath("$.pesel", is(patient.getPesel())))
                .andExpect(jsonPath("$.phone", is(patient.getPhone())))
                .andExpect(jsonPath("$.email", is(patient.getEmail())));

        verify(patientDAO, times(1)).findById(patientId);
        verify(patientDAO, only()).findById(patientId);
    }

    @Test
    void testShowPatientById_NonExistingPatient() throws Exception {
        // given
        Integer patientId = -1;

        when(patientDAO.findById(patientId)).thenReturn(null);

        // when, then
        mockMvc.perform(get(API_PATIENTS.concat(PATIENT_ID), patientId))
                .andExpect(status().isNotFound());

        verify(patientDAO, times(1)).findById(patientId);
        verify(patientDAO, only()).findById(patientId);
    }

    @Test
    void testShowHistory_ExistingPatientAndVisits() throws Exception {
        // given
        Integer patientId = 2;
        PatientDTO patient = DtoFixtures.somePatient2().withPatientId(patientId);
        VisitsDTO visitsList = VisitsDTO.of(DtoFixtures.visits.stream()
                .map(visit -> visit.withPatientId(patientId)).toList());

        when(patientDAO.findById(patientId)).thenReturn(patient);
        when(visitService.findAllVisitByPatient(patientId)).thenReturn(visitsList.getVisits());

        // when, then
        mockMvc.perform(get(API_PATIENTS.concat(HISTORY), patientId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visits[*]", notNullValue()))
                .andExpect(jsonPath("$.visits", hasSize(visitsList.getVisits().size())))
                .andExpect(jsonPath("$.visits[*].patientId", everyItem(is(patientId))));

        verify(patientDAO, times(1)).findById(patientId);
        verify(visitService, times(1)).findAllVisitByPatient(patientId);
        verifyNoMoreInteractions(patientDAO, visitService);
    }

    @Test
    void testShowHistory_NonExistingPatient() throws Exception {
        // given
        Integer patientId = -11;

        when(patientDAO.findById(patientId)).thenReturn(null);

        // when, then
        mockMvc.perform(get(API_PATIENTS.concat(HISTORY), patientId))
                .andExpect(status().isNotFound());

        verify(patientDAO, times(1)).findById(patientId);
        verify(patientDAO, only()).findById(patientId);
    }

    @Test
    void testShowHistory_ExistingPatientNoVisits() throws Exception {
        // given
        Integer patientId = 1;
        PatientDTO existingPatient = DtoFixtures.somePatient1();

        when(patientDAO.findById(patientId)).thenReturn(existingPatient);
        when(visitService.findAllVisitByPatient(patientId)).thenReturn(null);

        // when, then
        mockMvc.perform(get(API_PATIENTS.concat(HISTORY), patientId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.visits").isEmpty());

        verify(patientDAO, times(1)).findById(patientId);
        verify(visitService, times(1)).findAllVisitByPatient(patientId);
        verifyNoMoreInteractions(patientDAO, visitService);
    }

    @Test
    void testAddPatientShouldWorksCorrectly() throws Exception {
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

        // when, then
        mockMvc.perform(post(API_PATIENTS)
                        .param("name", name)
                        .param("surname", surname)
                        .param("pesel", pesel)
                        .param("phone", phone)
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("Dodano pacjenta: " + name + " " + surname));

        verify(patientDAO, times(1)).save(expectedPatientEntity);
        verify(patientDAO, only()).save(expectedPatientEntity);
    }

    @Test
    void testUpdatePatient_ExistingPatient() throws Exception {
        // given
        Integer patientId = 5;
        String name = "Janusz";
        String surname = "Wieczorkiewicz";
        String pesel = "77013112358";
        String phone = "+48 444 888 789";
        String email = "janusz@yaho.com";
        PatientEntity existingPatient = EntityFixtures.somePatient5();

        when(patientDAO.findEntityById(patientId)).thenReturn(existingPatient);

        // when, then
        mockMvc.perform(put(API_PATIENTS.concat(PATIENT_ID), patientId)
                        .param("name", name)
                        .param("surname", surname)
                        .param("pesel", pesel)
                        .param("phone", phone)
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("Aktualizacja wykonana"));

        verify(patientDAO, times(1)).save(existingPatient);
    }

    @Test
    void testUpdatePatient_NonExistingPatient() throws Exception {
        // given
        Integer patientId = -1;

        when(patientDAO.findEntityById(patientId)).thenReturn(null);

        // when, then
        mockMvc.perform(put(API_PATIENTS.concat(PATIENT_ID), patientId)
                        .param("name", "Adam")
                        .param("surname", "Kot")
                        .param("pesel", "66122014725")
                        .param("phone", "+48 900 900 900")
                        .param("email", "adam.kot@wp.pl"))
                .andExpect(status().isNotFound());

        verify(patientDAO, never()).save(ArgumentMatchers.any(PatientEntity.class));
    }

    @Test
    void testDeletePatient_ExistingPatient() throws Exception {
        // given
        Integer patientId = 4;
        PatientEntity existingPatient = EntityFixtures.somePatient4();

        when(patientDAO.findEntityById(patientId)).thenReturn(existingPatient);

        // when, then
        mockMvc.perform(delete(API_PATIENTS.concat(PATIENT_ID), patientId))
                .andExpect(status().isNoContent());
        verify(patientDAO, times(1)).delete(existingPatient);
        verify(patientDAO, times(1)).findEntityById(patientId);
        verifyNoMoreInteractions(patientDAO);
    }

    @Test
    void testDeletePatient_NonExistingPatient() throws Exception {
        // given
        Integer patientId = -1;

        when(patientDAO.findEntityById(patientId)).thenReturn(null);

        // when, then
        mockMvc.perform(delete(API_PATIENTS.concat(PATIENT_ID), patientId))
                .andExpect(status().isNotFound());

        verify(patientDAO, never()).delete(ArgumentMatchers.any(PatientEntity.class));
        verify(patientDAO, only()).findEntityById(patientId);
    }

    @Test
    void testUpdatePatientPhone_ExistingPatient() throws Exception {
        // given
        Integer patientId = 1;
        String newPhone = "+48 555 123 666";
        PatientEntity existingPatient = EntityFixtures.somePatient1();
        String expectedAnswer = String.format("Numer zaktualizowany na [%s] dla pacjenta [%s %s]", newPhone, existingPatient.getName(), existingPatient.getSurname());

        when(patientDAO.findEntityById(patientId)).thenReturn(existingPatient);

        // when, then
        mockMvc.perform(patch(API_PATIENTS.concat(PATIENT_UPDATE_PHONE), patientId)
                        .param("newPhone", newPhone))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedAnswer));

//        verify(patientDAO, times(1)).save(existingPatient);
        verify(patientDAO, times(1)).saveForUpdateContact(existingPatient);
        verify(patientDAO, times(1)).findEntityById(patientId);
        verifyNoMoreInteractions(patientDAO);
    }

    @Test
    void testUpdatePatientPhone_NonExistingPatient() throws Exception {
        // given
        Integer patientId = -12;
        String newPhone = "+48 555 123 777";

        when(patientDAO.findEntityById(patientId)).thenReturn(null);

        // when, then
        mockMvc.perform(patch(API_PATIENTS.concat(PATIENT_UPDATE_PHONE), patientId)
                        .param("newPhone", newPhone))
                .andExpect(status().isNotFound());

        verify(patientDAO, never()).save(ArgumentMatchers.any(PatientEntity.class));
        verify(patientDAO, only()).findEntityById(patientId);
    }

    @Test
    void testUpdatePatientEmail_ExistingPatient() throws Exception {
        // given
        Integer patientId = 2;
        String newEmail = "nowy.email@example.com";
        PatientEntity existingPatient = EntityFixtures.somePatient2();
        String expectedAnswer = String.format("Email zaktualizowany na [%s] dla pacjenta [%s %s]",
                newEmail, existingPatient.getName(), existingPatient.getSurname());

        when(patientDAO.findEntityById(patientId)).thenReturn(existingPatient);

        // when, then
        mockMvc.perform(patch(API_PATIENTS.concat(PATIENT_UPDATE_EMAIL), patientId)
                        .param("newEmail", newEmail))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedAnswer));

//        verify(patientDAO, times(1)).save(existingPatient);
        verify(patientDAO, times(1)).saveForUpdateContact(existingPatient);
        verify(patientDAO, times(1)).findEntityById(patientId);
        verifyNoMoreInteractions(patientDAO);
    }

    @Test
    void testUpdatePatientEmail_NonExistingPatient() throws Exception {
        // given
        Integer patientId = -1;
        String newEmail = "new.email@example.com";

        when(patientDAO.findEntityById(patientId)).thenReturn(null);

        // when, then
        mockMvc.perform(patch(API_PATIENTS.concat(PATIENT_UPDATE_EMAIL), patientId)
                        .param("newEmail", newEmail))
                .andExpect(status().isNotFound());

        verify(patientDAO, never()).save(ArgumentMatchers.any(PatientEntity.class));
        verify(patientDAO, only()).findEntityById(patientId);
    }

}