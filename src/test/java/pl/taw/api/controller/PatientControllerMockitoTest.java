package pl.taw.api.controller;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.util.UriComponentsBuilder;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.PatientsDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.security.ClinicUserDetailsService;
import pl.taw.infrastructure.security.RoleEntity;
import pl.taw.infrastructure.security.UserEntity;
import pl.taw.infrastructure.security.UserRepository;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.net.URI;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerMockitoTest {

    @Mock
    private PatientDAO patientDAO;

    @Mock
    private VisitService visitService;

    @Mock
    private OpinionDAO opinionDAO;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClinicUserDetailsService clinicUserDetailsService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private UserEntity userEntity;

    @InjectMocks
    private PatientController patientController;

    /**
     * Najprostszy test na pobieranie pacjenta przez kontroler
     * w21-25.
     * Test jednostkowy
     */
    @Test
    void thatRetrievingPatientsWorksCorrectly() {
        // given
        Integer patientId = 10;
        PatientDTO patient = DtoFixtures.somePatient1();
        when(patientDAO.findById(patientId)).thenReturn(patient);

        // when
        PatientDTO result = patientController.patientDetails(patientId);

        // then
        assertThat(result).isEqualTo(DtoFixtures.somePatient1());
    }

    /**
     * w 21-25.
     * Test jednostkowy
     */
    @Test
    void thatSavingPatientWorksCorrectlyNew() {
        // given
        PatientDTO patientDTO = DtoFixtures.somePatient1();
        PatientEntity patientEntity = EntityFixtures.somePatient1();

        when(patientDAO.saveAndReturn(any(PatientEntity.class))).thenReturn(patientEntity);

        // when
        ResponseEntity<?> result = patientController.addRequestPatient(patientDTO);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void testAddRequestPatient_Success() {
        // given
        PatientDTO inputPatientDTO = DtoFixtures.somePatient1();
        PatientEntity createdPatientEntity = EntityFixtures.somePatient1();

        when(patientDAO.saveAndReturn(any(PatientEntity.class))).thenReturn(createdPatientEntity);

        // when
        ResponseEntity<PatientDTO> response = patientController.addRequestPatient(inputPatientDTO);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        URI expectedLocation = UriComponentsBuilder.fromPath(
                "/patients/%s".formatted(createdPatientEntity.getPatientId())).build().toUri();
        assertEquals(expectedLocation, response.getHeaders().getLocation());
        verify(patientDAO, times(1)).saveAndReturn(any(PatientEntity.class));
    }

    @Test
    public void testAddRequestPatient_InvalidInput() {
        // given
        PatientDTO inputPatient = DtoFixtures.somePatient2().withPesel("501010");

        when(patientDAO.saveAndReturn(any(PatientEntity.class)))
                .thenThrow(new ConstraintViolationException("Validation failed", null));

        // when, then
        assertThrows(ConstraintViolationException.class,
                () -> patientController.addRequestPatient(inputPatient));
        verify(patientDAO, times(1)).saveAndReturn(any(PatientEntity.class));
    }


    @Test
    void thatSavingPatientWorksCorrectly() {
        // given
        PatientEntity patientEntity = EntityFixtures.somePatient1();
        PatientDTO patientDTO = DtoFixtures.somePatient1();

        when(patientDAO.saveAndReturn(any(PatientEntity.class))).thenReturn(patientEntity);

        // when
        ResponseEntity<PatientDTO> response = patientController.addRequestPatient(patientDTO);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        URI expectedLocation = UriComponentsBuilder.fromPath(
                "/patients/%s".formatted(patientEntity.getPatientId())).build().toUri();
        assertEquals(expectedLocation, response.getHeaders().getLocation());
        verify(patientDAO, times(1)).saveAndReturn(any(PatientEntity.class));
    }

    @Test
    public void testAddPatient_ContextLogin() {
        // given
        String name = "Stefan";
        String surname = "Kowalewski";
        String pesel = "80051574123";
        String phone = "+48 100 888 888";
        String email = "s.kowalewski@example.com";
        String context = "login";

        PatientEntity createdPatientEntity = PatientEntity.builder()
                .name(name)
                .surname(surname)
                .pesel(pesel)
                .phone(phone)
                .email(email)
                .build();

        when(patientDAO.saveAndReturn(any(PatientEntity.class))).thenReturn(createdPatientEntity.withPatientId(1));

        // when, then
        MockHttpServletRequest request = new MockHttpServletRequest();
        String redirectResult = patientController.addPatient(name, surname, pesel, phone, email, context, request);
        assertNotNull(redirectResult);
        assertEquals("redirect:dashboard/1", redirectResult);
        verify(patientDAO, times(1)).saveAndReturn(any(PatientEntity.class));
    }

    @Test
    public void testAddPatient_ContextNotLogin() {
        // given
        String name = "Adam";
        String surname = "Ryś";
        String pesel = "78101045689";
        String phone = "+48 123 456 789";
        String email = "a.rys@example.com";
        String context = "other";

        PatientEntity createdPatientEntity = PatientEntity.builder()
                .name(name)
                .surname(surname)
                .pesel(pesel)
                .phone(phone)
                .email(email)
                .build();

        when(patientDAO.saveAndReturn(any(PatientEntity.class))).thenReturn(createdPatientEntity.withPatientId(123));

        // when, then
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "http://example.com/somepage");
        String redirectResult = patientController.addPatient(name, surname, pesel, phone, email, context, request);
        assertNotNull(redirectResult);
        assertEquals("redirect:http://example.com/somepage", redirectResult);
        verify(patientDAO, times(1)).saveAndReturn(any(PatientEntity.class));
        verify(patientDAO, never()).findByPesel(anyString());
    }

    @Test
    public void testUpdatePatient_Success() {
        // given
        PatientDTO patientDTO = DtoFixtures.somePatient2();
        PatientEntity existingPatient = EntityFixtures.somePatient1();
        existingPatient.setName(patientDTO.getName());
        existingPatient.setSurname(patientDTO.getSurname());
        existingPatient.setPesel(patientDTO.getPesel());
        existingPatient.setPhone(patientDTO.getPhone());
        existingPatient.setEmail(patientDTO.getEmail());
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(patientDAO.findEntityById(patientDTO.getPatientId())).thenReturn(existingPatient);

        // when
        patientController.updatePatient(patientDTO, request);

        // then
        assertThat(patientDTO.getName()).isEqualTo(existingPatient.getName());
        assertThat(patientDTO.getSurname()).isEqualTo(existingPatient.getSurname());
        assertThat(patientDTO.getPesel()).isEqualTo(existingPatient.getPesel());
        assertThat(patientDTO.getPhone()).isEqualTo(existingPatient.getPhone());
        assertThat(patientDTO.getEmail()).isEqualTo(existingPatient.getEmail());

        verify(patientDAO, times(1)).save(existingPatient);
        verify(patientDAO, times(1)).findEntityById(patientDTO.getPatientId());
    }

    @Test
    public void testUpdateRequestPatient_Success() {
        // given
        Integer patientId = 1;
        PatientDTO patientDTO = DtoFixtures.somePatient2();

        PatientEntity existingPatient = EntityFixtures.somePatient1();
        existingPatient.setName(patientDTO.getName());
        existingPatient.setSurname(patientDTO.getSurname());
        existingPatient.setPesel(patientDTO.getPesel());
        existingPatient.setPhone(patientDTO.getPhone());
        existingPatient.setEmail(patientDTO.getEmail());

        when(patientDAO.findEntityById(patientId)).thenReturn(existingPatient);

        // when
        ResponseEntity<?> response = patientController.updateRequestPatient(patientId, patientDTO);

        // then
        assertThat(ResponseEntity.ok().build()).isEqualTo(response);
        assertThat(patientDTO.getName()).isEqualTo(existingPatient.getName());
        assertThat(patientDTO.getSurname()).isEqualTo(existingPatient.getSurname());
        assertThat(patientDTO.getPesel()).isEqualTo(existingPatient.getPesel());
        assertThat(patientDTO.getPhone()).isEqualTo(existingPatient.getPhone());
        assertThat(patientDTO.getEmail()).isEqualTo(existingPatient.getEmail());

        verify(patientDAO, times(1)).findEntityById(patientId);
        verify(patientDAO, times(1)).save(existingPatient);
    }

    @Test
    void shouldDeletePatientCorrectly() {
        // given
        Integer patientId = 3;
        PatientEntity existingPatient = EntityFixtures.somePatient3();

        when(patientDAO.findEntityById(patientId)).thenReturn(existingPatient);

        // when
        ResponseEntity<?> response = patientController.deletePatient(patientId);

        // then
        assertThat(response).isEqualTo(ResponseEntity.noContent().build());
        verify(patientDAO, times(1)).delete(existingPatient);
    }

    @Test
    public void testDeletePatient_NonExistingPatient() {
        // given
        Integer strangeId = -88;
        when(patientDAO.findEntityById(anyInt())).thenReturn(null);

        // when
        ResponseEntity<?> response = patientController.deletePatient(strangeId);

        // then
        assertThat(HttpStatus.NOT_FOUND).isEqualTo(response.getStatusCode());
        verify(patientDAO, never()).delete(any(PatientEntity.class));
    }

    @Test
    public void testDeletePatientById_ExistingPatient() {
        // given
        Integer patientId = 1;
        PatientEntity existingPatient = EntityFixtures.somePatient1();
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(patientDAO.findEntityById(anyInt())).thenReturn(existingPatient);

        // when
        ResponseEntity<String> response = patientController.deletePatientById(patientId, request);

        // then
        assertThat(HttpStatus.SEE_OTHER).isEqualTo(response.getStatusCode());
        verify(patientDAO, times(1)).findEntityById(patientId);
        verify(patientDAO, times(1)).delete(existingPatient);
    }

    @Test
    public void testPatientsList() {
        // given
        List<PatientDTO> patients = DtoFixtures.patients;

        when(patientDAO.findAll()).thenReturn(patients);

        // when
        List<PatientDTO> result = patientController.patientsList();

        // then
        assertEquals(patients.size(), result.size());
        verify(patientDAO, times(1)).findAll();
    }

    @Test
    void testPatientsDTOList() {
        // given
        PatientsDTO patientsDTO = PatientsDTO.of(DtoFixtures.patients);

        when(patientDAO.findAll()).thenReturn(patientsDTO.getPatients());

        // when
        PatientsDTO result = patientController.patientsDTOList();

        // then
        assertEquals(patientsDTO.getPatients().size(), result.getPatients().size());
        verify(patientDAO, times(1)).findAll();
    }

    @Test
    public void testShowPatient() {
        // given
        Integer patientId = 1;
        PatientDTO dummyPatient = DtoFixtures.somePatient1();
        List<VisitDTO> dummyVisits = DtoFixtures.visits.stream().map(visit -> visit.withPatientId(patientId)).toList();
        List<OpinionDTO> dummyOpinions = DtoFixtures.opinions.stream().map(opinion -> opinion.withPatient(dummyPatient)).toList();

        when(patientDAO.findById(patientId)).thenReturn(dummyPatient);
        when(visitService.findAllVisitByPatient(patientId)).thenReturn(dummyVisits);
        when(opinionDAO.findAllByPatient(patientId)).thenReturn(dummyOpinions);

        // when
        String viewName = patientController.showPatient(patientId, model);

        // then
        assertEquals("patient/patient-show", viewName);

        verify(model, times(1)).addAttribute(eq("patient"), eq(dummyPatient));
        verify(model, times(1)).addAttribute(eq("visits"), eq(dummyVisits));
        verify(model, times(1)).addAttribute(eq("opinions"), eq(dummyOpinions));

        verify(patientDAO, times(1)).findById(patientId);
        verify(visitService, times(1)).findAllVisitByPatient(patientId);
        verify(opinionDAO, times(1)).findAllByPatient(patientId);
    }

    @Test
    public void testPatientsPanel() {
        // given
        List<PatientDTO> patients = DtoFixtures.patients;
        String username = "testUser";

        when(patientDAO.findAll()).thenReturn(patients);
        when(authentication.getName()).thenReturn(username);

        // when
        String viewName = patientController.patientsPanel(model, authentication);

        // then
        assertEquals("patient/patient-panel", viewName);

        verify(model, times(1)).addAttribute(eq("patients"), eq(patients));
        verify(model, times(1)).addAttribute(eq("updatePatient"), any(PatientDTO.class));
        verify(model, times(1)).addAttribute("username", username);

        verify(patientDAO, times(1)).findAll();
    }

    @Test
    public void testPatientPage() {
        // given
        UserEntity userEntity = EntityFixtures.someUser1();
        PatientDTO patientDTO = DtoFixtures.somePatient1();

        when(authentication.getName()).thenReturn("username");
        when(userRepository.findByUserName(anyString())).thenReturn(userEntity);
        when(patientDAO.findByEmail(anyString())).thenReturn(patientDTO);

        // when
        String result = patientController.patientPage(authentication, model);

        // then
        assertEquals("patient/patient-dashboard", result);
        verify(model).addAttribute(eq("patient"), any(PatientDTO.class));
        verify(authentication, times(2)).getName();
        verify(userRepository, times(2)).findByUserName(anyString());
        verify(patientDAO, times(1)).findByEmail(anyString());
    }

    @Test
    public void testShowDashboardWithId() {
        // given
        Integer patientId = 2;
        PatientDTO patientDTO = DtoFixtures.somePatient2();

        when(patientDAO.findById(patientId)).thenReturn(patientDTO);

        // when
        String result = patientController.showDashboardWithId(patientId, model);

        // then
        assertEquals("patient/patient-dashboard", result);
        verify(model).addAttribute(eq("patient"), any(PatientDTO.class));
        verify(patientDAO, times(1)).findById(patientId);
    }

    @Test
    public void testShowHistory() {
        // given
        Integer patientId = 1;
        PatientDTO patientDTO = DtoFixtures.somePatient1();
        List<VisitDTO> visits = DtoFixtures.visits;

        when(patientDAO.findById(patientId)).thenReturn(patientDTO);
        when(visitService.findAllVisitByPatient(patientId)).thenReturn(visits);
        when(authentication.getName()).thenReturn("username");

        // when
        String result = patientController.showHistory(patientId, authentication, model);

        // then
        assertEquals("patient/patient-history", result);
        verify(model).addAttribute(eq("username"), eq("username"));
        verify(model).addAttribute(eq("patient"), any(PatientDTO.class));
        verify(model).addAttribute("patient", patientDTO);
        verify(model).addAttribute(eq("visits"), anyList());
        verify(model).addAttribute("visits", visits);
        verify(patientDAO, times(1)).findById(patientId);
        verify(visitService, times(1)).findAllVisitByPatient(patientId);
        verify(authentication, times(1)).getName();
    }

    @Test
    public void testShowUpdatePhoneView() {
        // given
        Integer patientId = 1;
        PatientDTO patientDTO = DtoFixtures.somePatient1();

        when(patientDAO.findById(patientId)).thenReturn(patientDTO);

        // when
        String result = patientController.showUpdatePhoneView(patientId, model);

        // then
        assertEquals("core/update-phone", result);
        verify(model).addAttribute(eq("patient"), any(PatientDTO.class));
        verify(model).addAttribute("patient", patientDTO);
        verify(patientDAO, times(1)).findById(patientId);
    }

    @Test
    public void testUpdatePatientPhoneView() {
        // given
        Integer patientId = 4;
        String newPhone = "+48 123 456 000";
        String referer = "/some-referer";
        PatientEntity patientEntity = EntityFixtures.somePatient4();

        when(patientDAO.findEntityById(patientId)).thenReturn(patientEntity);

        // when
        String result = patientController.updatePatientPhoneView(patientId, newPhone, referer);

        // then
        assertEquals("redirect:" + referer, result);
        assertEquals(newPhone, patientEntity.getPhone());
        verify(patientDAO, times(1)).save(patientEntity);
    }

    @Test
    public void testUpdatePatientPhoneViewWithoutReferer() {
        // given
        Integer patientId = 1;
        String newPhone = "+48 123 456 001";
        PatientEntity patientEntity = EntityFixtures.somePatient1();

        when(patientDAO.findEntityById(patientId)).thenReturn(patientEntity);

        // when
        String result = patientController.updatePatientPhoneView(patientId, newPhone, null);

        // then
        assertEquals("redirect:/patients/dashboard/" + patientId, result);
        assertEquals(newPhone, patientEntity.getPhone());
        verify(patientDAO, times(1)).save(patientEntity);
    }

    @Test
    public void testUpdatePhoneView() {
        // given
        UserEntity user = EntityFixtures.someUser1();
        String userName = user.getUserName();
        PatientDTO patient = DtoFixtures.somePatient1();

        when(userRepository.findByUserName(userName)).thenReturn(user);
        when(authentication.getName()).thenReturn(userName);
        when(patientDAO.findByEmail(user.getEmail())).thenReturn(patient);

        // when
        String result = patientController.updatePhoneView(authentication, model);

        // then
        assertEquals("patient/patient-phone", result);
        verify(model).addAttribute(eq("patient"), any(PatientDTO.class));
        verify(userRepository, times(2)).findByUserName(userName);
        verify(authentication, times(2)).getName();
        verify(patientDAO, times(1)).findByEmail(user.getEmail());
        verifyNoMoreInteractions(model, userRepository, authentication, patientDAO);
    }

    @Test
    public void testUpdatePhoneNumber() {
        // given
        Integer patientId = 1;
        PatientDTO patientDTO = DtoFixtures.somePatient2().withPatientId(patientId);
        String newPhoneNumber = patientDTO.getPhone();
        PatientEntity patientEntity = EntityFixtures.somePatient2().withPatientId(patientId);

        when(patientDAO.findEntityById(patientId)).thenReturn(patientEntity);

        // when
        String result = patientController.updatePhoneNumber(patientDTO);

        // then
        assertEquals("redirect:/", result);
        assertEquals(newPhoneNumber, patientEntity.getPhone());

        verify(patientDAO, times(1)).findEntityById(patientId);
        verify(patientDAO, times(1)).saveForUpdateContact(patientEntity);
        verifyNoMoreInteractions(patientDAO);
    }

    @Test
    public void testUpdateEmailView() {
        // given
        UserEntity user = EntityFixtures.someUser1();
        String userName = user.getUserName();
        PatientDTO patient = DtoFixtures.somePatient1().withEmail(user.getEmail());

        when(userRepository.findByUserName(userName)).thenReturn(user);
        when(authentication.getName()).thenReturn(userName);
        when(patientDAO.findByEmail(user.getEmail())).thenReturn(patient);

        // when
        String result = patientController.updateEmailView(authentication, model);

        // then
        assertEquals("patient/patient-email", result);

        verify(model).addAttribute(eq("patient"), eq(patient));
        verify(userRepository, times(2)).findByUserName(userName);
        verify(authentication, times(2)).getName();
        verify(patientDAO, times(1)).findByEmail(user.getEmail());
        verifyNoMoreInteractions(model, userRepository, authentication, patientDAO);
    }

    // do zrobienia ostatnia metoda!!!


}