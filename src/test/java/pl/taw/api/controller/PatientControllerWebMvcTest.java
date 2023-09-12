package pl.taw.api.controller;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.jpa.PatientJpaRepository;
import pl.taw.infrastructure.security.ClinicUserDetailsService;
import pl.taw.infrastructure.security.UserEntity;
import pl.taw.infrastructure.security.UserRepository;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.taw.api.controller.PatientController.*;

/**
 * Materiał w21-26
 */

@WebMvcTest(controllers = PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PatientControllerWebMvcTest {

    private final MockMvc mockMvc;  // symuluje wywoływanie endpoint-ów po stronie przeglądarki (curl)

    @MockBean
    @SuppressWarnings("unused")
    private PatientJpaRepository patientJpaRepository;
    @MockBean
    @SuppressWarnings("unused")
    private PatientDAO patientDAO;
    @MockBean
    @SuppressWarnings("unused")
    private OpinionDAO opinionDAO;
    @MockBean
    @SuppressWarnings("unused")
    private VisitService visitService;
    @MockBean
    @SuppressWarnings("unused")
    private UserRepository userRepository;

    @MockBean
    @SuppressWarnings("unused")
    private Authentication authentication;
    @MockBean
    @SuppressWarnings("unused")
    private UserDetails userDetails;

    @MockBean
    @SuppressWarnings("unused")
    private ClinicUserDetailsService clinicUserDetailsService;

    @Test
    void thatEmailValidationWorksCorrectly() throws Exception {
        // given
        final String request = """
                {
                    "email": "badEmail"
                }
                """;

        // when, then
        mockMvc.perform(
                post(PATIENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", Matchers.containsString("badEmail")))
                .andExpect(model().attributeExists("hint"))
                .andExpect(view().name("error"));
    }

    @Test
    void shouldShowPatientDetails() throws Exception {
        // given
        int patientId = 1;
        PatientDTO patientDTO = DtoFixtures.somePatient1();
        DoctorDTO doctorDTO = DtoFixtures.someDoctor1();
        List<VisitDTO> visits = DtoFixtures.visits.stream().map(visit -> visit.withPatientId(patientId).withPatient(patientDTO).withDoctor(doctorDTO)).toList();
        List<OpinionDTO> opinions = DtoFixtures.opinions;

        when(patientDAO.findById(patientId)).thenReturn(patientDTO);
        when(visitService.findAllVisitByPatient(patientId)).thenReturn(visits);
        when(opinionDAO.findAllByPatient(patientId)).thenReturn(opinions);

        // when, then
        mockMvc.perform(get(PATIENTS.concat(SHOW), patientId))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patient-show"))
                .andExpect(model().attribute("patient", patientDTO))
                .andExpect(model().attribute("visits", visits))
                .andExpect(model().attribute("opinions", opinions));

        verify(patientDAO, times(1)).findById(patientId);
        verify(visitService, times(1)).findAllVisitByPatient(patientId);
        verify(opinionDAO, times(1)).findAllByPatient(patientId);
        verifyNoMoreInteractions(patientDAO, visitService, opinionDAO);
    }

    @Test
    void thatPatientCanBeRetrievedCorrectly() throws Exception {
        // given
        int patientId = 1;
        PatientDTO patientDTO = DtoFixtures.somePatient1();

        when(patientDAO.findById(patientId)).thenReturn(patientDTO);

        // when, then
        mockMvc.perform(get(PATIENTS.concat(PATIENT_ID), patientId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId", Matchers.is(patientDTO.getPatientId())))
                .andExpect(jsonPath("$.name", Matchers.is(patientDTO.getName())))
                .andExpect(jsonPath("$.surname", Matchers.is(patientDTO.getSurname())))
                .andExpect(jsonPath("$.pesel", Matchers.is(patientDTO.getPesel())))
                .andExpect(jsonPath("$.phone", Matchers.is(patientDTO.getPhone())))
                .andExpect(jsonPath("$.email", Matchers.is(patientDTO.getEmail())));

        verify(patientDAO, times(1)).findById(patientId);
        verify(patientDAO, only()).findById(patientId);
    }

    @ParameterizedTest
    @MethodSource
    void thatPhoneValidationWorksCorrectly(Boolean correctPhone, String phone) throws Exception {
        // given
        final var request = """
                {
                    "phone": "%s"
                }
                """.formatted(phone);
        PatientEntity patient = EntityFixtures.somePatient1();

        when(patientDAO.saveAndReturn(any(PatientEntity.class))).thenReturn(patient);

        // when, then
        if (correctPhone) {
            String expectedRedirect = PATIENTS.concat(PATIENT_ID_RESULT).formatted(patient.getPatientId());
            mockMvc.perform(post(PATIENTS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isCreated())
                    .andExpect(redirectedUrl(expectedRedirect));

            verify(patientDAO, times(1)).saveAndReturn(any(PatientEntity.class));
            verify(patientDAO, only()).saveAndReturn(any(PatientEntity.class));
        } else {
            mockMvc.perform(post(PATIENTS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(model().attributeExists("errorMessage", "status", "hint"))
                    .andExpect(view().name("error"))
                    .andExpect(model().attribute("status", "400"))
                    .andExpect(model().attribute("hint", "Numer powinien być w formacie [+48 xxx xxx xxx]"));

            verifyNoInteractions(patientDAO);
        }
    }

    public static Stream<Arguments> thatPhoneValidationWorksCorrectly() {
        return Stream.of(
                Arguments.of(false, ""),
                Arguments.of(false, "+48 504 203 260@@"),
                Arguments.of(false, "+48.504.203.260"),
                Arguments.of(false, "+55(123) 456-78-90-"),
                Arguments.of(false, "+55(123) - 456-78-90"),
                Arguments.of(false, "504.203.260"),
                Arguments.of(false, " "),
                Arguments.of(false, "-"),
                Arguments.of(false, "()"),
                Arguments.of(false, "() + ()"),
                Arguments.of(false, "(21 777"),
                Arguments.of(false, "+48 (21)"),
                Arguments.of(false, "(+)"),
                Arguments.of(false, " 1"),
                Arguments.of(false, "1"),
                Arguments.of(false, "+48 (12) 504 203 260"),
                Arguments.of(false, "+48 (12) 504-203-260"),
                Arguments.of(false, "+48(12)504203260"),
                Arguments.of(false, "555-5555-555"),
                Arguments.of(true, "+48 504 203 260")
        );
    }

    @Test
    public void testPatientDetails() throws Exception {
        // given
        Integer patientId = 1;
        PatientDTO expectedPatientDTO = DtoFixtures.somePatient1();

        when(patientDAO.findById(patientId)).thenReturn(expectedPatientDTO);

        // when, then
        mockMvc.perform(get(PATIENTS.concat(PATIENT_ID), patientId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.patientId", Matchers.is(expectedPatientDTO.getPatientId())))
                .andExpect(jsonPath("$.name", Matchers.is(expectedPatientDTO.getName())))
                .andExpect(jsonPath("$.surname", Matchers.is(expectedPatientDTO.getSurname())))
                .andExpect(jsonPath("$.pesel", Matchers.is(expectedPatientDTO.getPesel())))
                .andExpect(jsonPath("$.phone", Matchers.is(expectedPatientDTO.getPhone())))
                .andExpect(jsonPath("$.email", Matchers.is(expectedPatientDTO.getEmail())))
                .andExpect(jsonPath("$.patientId").value(expectedPatientDTO.getPatientId()));

        verify(patientDAO, times(1)).findById(patientId);
        verify(patientDAO, only()).findById(patientId);
    }

    @Test
    public void testUpdatePatientPhone() throws Exception {
        // given
        Integer patientId = 1;
        String newPhone = "+48 147 147 147";
        PatientEntity existingPatient = EntityFixtures.somePatient3().withPatientId(patientId);
        String referer = "/patients/panel";

        when(patientDAO.findEntityById(patientId)).thenReturn(existingPatient);

        // when, then
        mockMvc.perform(post(PATIENTS.concat(PATIENT_UPDATE_PHONE), patientId)
                        .param("newPhone", newPhone)
                        .param("referer", referer))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(referer));

        MatcherAssert.assertThat(existingPatient.getPhone(), Matchers.containsString(newPhone));
        Assertions.assertThat(existingPatient.getPhone()).isEqualTo(newPhone);

        verify(patientDAO, times(1)).findEntityById(patientId);
        verify(patientDAO, times(1)).save(existingPatient);
        verifyNoMoreInteractions(patientDAO);
    }

    @Test
    public void testUpdatePhoneView() throws Exception {
        // given
        Integer patientId = 1;
        PatientEntity existingPatient = EntityFixtures.somePatient3().withPatientId(patientId);
        PatientDTO patient = DtoFixtures.somePatient2().withPatientId(patientId);
        UserEntity user = EntityFixtures.someUser1();
        String referer = "/patients/panel";
//        Authentication authentication = mock(Authentication.class);
//        UserDetails details =


//        when(userDetails.getAuthorities()).thenReturn(user.getRoles().stream().findAny());
        when(authentication.getName()).thenReturn("Stanisław");
//        when(clinicUserDetailsService.loadUserByUsername(authentication.getName())).thenReturn(userDetails);
        when(clinicUserDetailsService.loadUserByUsername(authentication.getName())).thenReturn(userDetails);
        when(clinicUserDetailsService.getUserEmailAfterAuthentication(authentication.getName())).thenReturn("stachu@wp.pl");
        when(patientDAO.findById(patientId)).thenReturn(patient);

        MockHttpServletRequestBuilder getRequest = get(PATIENTS.concat(PHONE_VIEW)); // Zastąp '/your-endpoint' adresem URL twojej metody
        getRequest.principal(authentication);

        // when, then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk()) // Oczekuj statusu HTTP 200 OK
                .andExpect(model().attributeExists("patient")); // Oczekuj, że model zawiera atrybut "patient"
    }

}
