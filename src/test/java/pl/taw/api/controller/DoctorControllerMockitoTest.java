package pl.taw.api.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.business.DoctorService;
import pl.taw.business.WorkingHours;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.WorkingHoursFixtures;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorControllerMockitoTest {

    @InjectMocks
    private DoctorController doctorController;

    @Mock
    private DoctorDAO doctorDAO;

    @Mock
    private OpinionDAO opinionDAO;

    @Mock
    private DoctorService doctorService;

    @MockBean
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private HttpServletRequest request;

    private Validator validator;

    @BeforeEach
    void setup() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("stefano", "pass123", List.of(new SimpleGrantedAuthority("USER")))
        );
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void doctorsPanelWorksCorrectly() {
        // given
        List<DoctorDTO> doctors = DtoFixtures.doctors;
        String username = "stefano";
        ExtendedModelMap model = new ExtendedModelMap();

        when(doctorDAO.findAll()).thenReturn(doctors);

        // when
        String result = doctorController.doctorsPanel(model);

        // then
        assertThat(result).isEqualTo("doctor/doctor-panel");
        assertThat(model.getAttribute("doctors")).isEqualTo(doctors);
        assertThat(model.getAttribute("username")).isEqualTo(username);
        assertEquals(new DoctorDTO(), model.getAttribute("updateDoctor"));
    }

    @Test
    void addDoctorShouldSaveDoctorAndRedirectToReferer() {
        // given
        String name = "Alojzy";
        String surname = "Nowak";
        String title = "Pediatra";
        String phone = "+48 120 333 555";
        String email = "alojz@eclinic.pl";

        when(request.getHeader("Referer")).thenReturn("http://eclinic.com/doctors/panel");

        // given
        String redirectUrl = doctorController.addDoctor(name, surname, title, phone, email, request);

        // then
        verify(doctorDAO).save(any(DoctorEntity.class));
        assertEquals("redirect:http://eclinic.com/doctors/panel", redirectUrl);
    }

    @Test
    void addValidDoctorShouldSaveDoctorAndRedirectToReferer() {
        // given
        DoctorDTO doctorDTO = DoctorDTO.builder()
                .name("Franek")
                .surname("Kimono")
                .title("Psychiatra")
                .phone("+48 120 888 888")
                .email("franekkimono@eclinic.pl")
                .build();

        when(request.getHeader("Referer")).thenReturn("http://eclinic.com/doctors/add/valid");

        // when
        Set<ConstraintViolation<DoctorDTO>> violations = validator.validate(doctorDTO);
        String redirectUrl = doctorController.addValidDoctor(doctorDTO, request);

        // then
        org.junit.jupiter.api.Assertions.assertTrue(violations.isEmpty());
        verify(doctorDAO).save(any(DoctorEntity.class));
        assertEquals("redirect:http://eclinic.com/doctors/add/valid", redirectUrl);
    }

    @Test
    void addValidDoctorShouldNotSaveInvalidDoctor() {
        // given
        DoctorDTO doctorDTO = DoctorDTO.builder()
                .name("Franek")
                .surname("Kimono")
                .title("Psychiatra")
                .phone("+48 120 888 888")
                .email("franek@kimono@eclinic.pl")
                .build();

        // when
        Set<ConstraintViolation<DoctorDTO>> violations = validator.validate(doctorDTO);
        String redirectUrl = doctorController.addValidDoctor(doctorDTO, request);

        // then
        assertFalse(violations.isEmpty());
        assertNull(redirectUrl);
//        verify(doctorDAO, never()).save(any(DoctorEntity.class));
//        verify(doctorDAO, never()).save(any());
//        verifyNoInteractions(doctorDAO);
    }

    @Test
    void testUpdateDoctor() {
        // given
        int doctorId = 5;
        DoctorDTO updateDoctor = DoctorDTO.builder()
                .doctorId(doctorId)
                .name("Konstanty")
                .surname("Kosowski")
                .title("Chirurg")
                .phone("+48 120 147 888")
                .email("konstanty@eclinic.pl")
                .build();

        DoctorEntity doctorEntity = DoctorEntity.builder().doctorId(doctorId).build();
        when(doctorDAO.findEntityById(updateDoctor.getDoctorId())).thenReturn(doctorEntity);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "/doctors/panel");

        // Method under test
        String viewName = doctorController.updateDoctor(updateDoctor, request);

        // Verification
        assertEquals("redirect:/doctors/panel", viewName);
        assertEquals(updateDoctor.getDoctorId(), doctorEntity.getDoctorId());
        assertEquals(updateDoctor.getSurname(), doctorEntity.getSurname());
        assertEquals(updateDoctor.getEmail(), doctorEntity.getEmail());
        verify(doctorDAO).save(doctorEntity);
    }

    @Test
    void testDeleteDoctorById() {
        // given
        int doctorId = 1;
        DoctorEntity doctorEntity = new DoctorEntity().withDoctorId(doctorId);
        when(doctorDAO.findEntityById(doctorId)).thenReturn(doctorEntity);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "/doctors/panel");

        // when
        String viewName = doctorController.deleteDoctorById(doctorId, request);

        // then
        assertEquals("redirect:/doctors/panel", viewName);
        verify(doctorDAO).delete(doctorEntity);
    }

    @Test
    void testDeleteDoctorById_ThrowsEntityNotFoundException() {
        // given
        int doctorId = 1;
        when(doctorDAO.findEntityById(doctorId)).thenReturn(null);

        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        assertThrows(EntityNotFoundException.class, () -> doctorController.deleteDoctorById(doctorId, request));

        // then
        verify(doctorDAO, never()).delete(any());
    }

    @Test
    void testDoctorsBySpecializationsView() {
        // given
        String specialization = "Lekarz rodzinny";
        List<DoctorDTO> expectedList = Arrays.asList(DtoFixtures.someDoctor1(), DtoFixtures.someDoctor2());
        ExtendedModelMap model = new ExtendedModelMap();

        when(doctorDAO.findBySpecialization(specialization)).thenReturn(expectedList);

        // when
        String viewName = doctorController.doctorsBySpecializationsView(specialization, model);

        // then
        assertEquals("doctor/doctors-specialization", viewName);
        assertEquals(expectedList, model.getAttribute("doctors"));
        assertEquals(specialization, model.getAttribute("specialization"));
    }

    @Test
    void testSpecializations() {
        // given
        List<String> expectedSpecializations = Arrays.asList("Dermatolog", "Lekarz rodzinny", "Psychiatra");
        List<DoctorDTO> expectedList = Arrays.asList(
                DtoFixtures.someDoctor1().withTitle("Dermatolog"),
                DtoFixtures.someDoctor2(), DtoFixtures.someDoctor3());
        ExtendedModelMap model = new ExtendedModelMap();

        when(doctorDAO.findAll()).thenReturn(expectedList);

        // when
        String viewName = doctorController.specializations(model);

        // then
        assertEquals("doctor/specializations", viewName);
        assertEquals(expectedSpecializations, model.getAttribute("specializations"));
    }

    @Test
    void testShowDoctorDetails() throws IOException {
        // given
        int doctorId = 1;
        DoctorDTO mockDoctor = new DoctorDTO().withDoctorId(doctorId);
        when(doctorDAO.findById(doctorId)).thenReturn(mockDoctor);

        List<OpinionDTO> opinions = Arrays.asList(DtoFixtures.someOpinion1(), DtoFixtures.someOpinion2().withDoctorId(doctorId));
        when(opinionDAO.findAllByDoctor(doctorId)).thenReturn(opinions);

        ExtendedModelMap model = new ExtendedModelMap();
        List<WorkingHours> mockWorkingHours = Arrays.asList(
                WorkingHoursFixtures.mondayHours(), WorkingHoursFixtures.fridayHours());

        when(doctorService.getWorkingHours(doctorId)).thenReturn(mockWorkingHours);

        String username = "testUser";
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null);

        // given
        String viewName = doctorController.showDoctorDetails(doctorId, model, authentication);

        // then
        assertEquals("doctor/doctor-show-new", viewName);
        assertEquals(mockDoctor, model.getAttribute("doctor"));
        assertEquals(mockWorkingHours, model.getAttribute("workingHours"));
        assertEquals(username, model.getAttribute("username"));
        assertEquals(opinions, model.getAttribute("opinions"));
    }

    @Test
    void testDoctors() {
        // given
        List<DoctorDTO> doctors = DtoFixtures.doctors;
        ExtendedModelMap model = new ExtendedModelMap();

        when(doctorDAO.findAll()).thenReturn(doctors);

        // when
        String result = doctorController.doctors(model);

        // then
        assertEquals(doctors, model.getAttribute("doctors"));
        assertEquals("doctor/doctors-logo", result);
    }
}