package pl.taw.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.util.DtoFixtures;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorControllerTest {

    @InjectMocks
    private DoctorController doctorController;

    @Mock
    private DoctorDAO doctorDAO;

    @MockBean
    private Authentication authentication;

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
    void updateDoctor() {
    }

    @Test
    void deleteDoctorById() {
    }

    @Test
    void doctorsBySpecializationsView() {
    }

    @Test
    void specializations() {
    }

    @Test
    void showDoctorDetails() {
    }

    @Test
    void doctors() {
    }
}