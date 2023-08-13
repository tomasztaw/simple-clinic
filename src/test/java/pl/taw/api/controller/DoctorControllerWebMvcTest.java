package pl.taw.api.controller;

import lombok.AllArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.business.DoctorService;
import pl.taw.business.ReservationService;
import pl.taw.business.WorkingHours;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.security.UserRepository;
import pl.taw.util.DtoFixtures;
import pl.taw.util.WorkingHoursFixtures;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.taw.api.controller.DoctorController.*;

@WebMvcTest(controllers = DoctorController.class)
@AutoConfigureMockMvc(addFilters = false) // wyłączenie filtrów security dla testów Mock Mvc
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DoctorControllerWebMvcTest {

    private MockMvc mockMvc; // symuluje wywołania przeglądarki

    @MockBean
    private final DoctorDAO doctorDAO;

    @MockBean
    private final DoctorService doctorService;

    @MockBean
    private final ReservationService reservationService;

    @MockBean
    private final PatientDAO patientDAO;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final OpinionDAO opinionDAO;

    @MockBean
    private final Authentication authentication;

    @BeforeEach
    void setup() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("stefano", "pass123", List.of(new SimpleGrantedAuthority("USER")))
        );
    }

    @Test
    @WithMockUser
    public void testDoctorsPanel() throws Exception {
        // given
        List<DoctorDTO> doctors = DtoFixtures.doctors;

        when(doctorDAO.findAll()).thenReturn(doctors);

        // when, then
        mockMvc.perform(get(DOCTORS.concat(PANEL)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("doctors", doctors))
                .andExpect(model().attributeExists("updateDoctor"))
                .andExpect(model().attributeExists("username"))
                .andExpect(view().name("doctor/doctor-panel"));

        verify(doctorDAO, times(1)).findAll();
        verify(doctorDAO, only()).findAll();
    }

    @Test
    @WithMockUser
    public void testDoctorsBySpecializationsView() throws Exception {
        // given
        String specialization = "Lekarz rodzinny";
        List<DoctorDTO> doctors = Arrays.asList(DtoFixtures.someDoctor1(), DtoFixtures.someDoctor2());

        when(doctorDAO.findBySpecialization(specialization)).thenReturn(doctors);

        // when, then
        mockMvc.perform(get(DOCTORS.concat(SPECIALIZATION), specialization))
                .andExpect(status().isOk())
                .andExpect(model().attribute("doctors", doctors))
                .andExpect(model().attribute("specialization", specialization))
                .andExpect(view().name("doctor/doctors-specialization"));

        verify(doctorDAO, times(1)).findBySpecialization(specialization);
        verify(doctorDAO, only()).findBySpecialization(specialization);
    }

    @Test
    @WithMockUser
    public void testSpecializations() throws Exception {
        // given
        List<String> specializations = List.of("Lekarz rodzinny", "Psychiatra");
        List<DoctorDTO> doctors = DtoFixtures.doctors;

        when(doctorDAO.findAll()).thenReturn(doctors);

        // when, then
        mockMvc.perform(get(DOCTORS.concat(SPECIALIZATIONS)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("specializations", specializations))
                .andExpect(view().name("doctor/specializations"));

        verify(doctorDAO, times(1)).findAll();
        verify(doctorDAO, only()).findAll();
    }

    @Test
    @WithMockUser
    public void testShowDoctorDetails() throws Exception {
        // given
        Integer doctorId = 1;
        DoctorDTO doctor = DtoFixtures.someDoctor1();
        List<WorkingHours> workingHours = Arrays.asList(
                WorkingHoursFixtures.mondayHours(), WorkingHoursFixtures.fridayHours());
        List<OpinionDTO> opinions = Arrays.asList(DtoFixtures.someOpinion1(), DtoFixtures.someOpinion2().withDoctorId(doctorId));

        when(doctorDAO.findById(doctorId)).thenReturn(doctor);
        when(doctorService.getWorkingHours(doctorId)).thenReturn(workingHours);
        when(opinionDAO.findAllByDoctor(doctorId)).thenReturn(opinions);
        when(authentication.getName()).thenReturn("testUser");

        // when, then
        mockMvc.perform(get(DOCTORS.concat(SHOW), doctorId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("doctor", doctor))
                .andExpect(model().attribute("workingHours", workingHours))
                .andExpect(model().attribute("opinions", opinions))
                .andExpect(view().name("doctor/doctor-show"));

        verify(doctorDAO, times(1)).findById(doctorId);
        verify(doctorService, times(1)).getWorkingHours(doctorId);
        verify(opinionDAO, times(1)).findAllByDoctor(doctorId);
        verifyNoMoreInteractions(doctorDAO, doctorService, opinionDAO);
    }

    @Test
    void doctors() throws Exception {
        // given
        List<DoctorDTO> doctors = DtoFixtures.doctors;

        when(doctorDAO.findAll()).thenReturn(doctors);

        // when, then
        mockMvc.perform(get(DOCTORS))
                .andExpect(status().isOk())
                .andExpect(model().attribute("doctors", doctors))
                .andExpect(view().name("doctor/doctors-all"));

        verify(doctorDAO, times(1)).findAll();
        verify(doctorDAO, only()).findAll();
    }

//    @Disabled("Nie mogę dojść jak to ogarnąć z csrf")
    @ParameterizedTest
    @MethodSource
//    @WithMockUser(authorities = "ADMIN")
    @WithMockUser(authorities = "USER")
    void thatPhoneValidationWorksCorrectly(Boolean correctPhone, String phone) throws Exception {
        // given
        LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        Map<String, String> parametersMap = DoctorDTO.builder().phone(phone).build().asMap();
        parametersMap.forEach(parameters::add);

        // when, then
        if (correctPhone) {
            mockMvc.perform(post("/doctors/add/valid").params(parameters))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeDoesNotExist("errorMessage"))
                    .andExpect(view().name("home"));
//                    .andExpect(view().name("doctor/doctor-panel"));
        } else {
            mockMvc.perform(post("/doctors/add/valid").params(parameters))
                    .andExpect(status().isBadRequest())
                    .andExpect(model().attributeExists("errorMessage"))
                    .andExpect(model().attribute("errorMessage", Matchers.containsString(phone)))
                    .andExpect(view().name("error"));
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
}