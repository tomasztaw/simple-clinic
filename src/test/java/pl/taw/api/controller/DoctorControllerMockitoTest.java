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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.business.DoctorService;
import pl.taw.business.WorkingHours;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;
import pl.taw.util.WorkingHoursFixtures;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        verify(doctorDAO, times(1)).findAll();
        verify(doctorDAO, only()).findAll();
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
        assertEquals("redirect:http://eclinic.com/doctors/panel", redirectUrl);

        verify(doctorDAO, times(1)).save(any(DoctorEntity.class));
        verify(doctorDAO, only()).save(any(DoctorEntity.class));
    }

    @Test
    void addValidDoctorShouldSaveDoctorAndRedirectToReferer() throws BindException {
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
        BindingResult bindingResult = new BeanPropertyBindingResult(doctorDTO, "updateDoctor");
        String redirectUrl = doctorController.addValidDoctor(doctorDTO, bindingResult,  request);

        // then
        org.junit.jupiter.api.Assertions.assertTrue(violations.isEmpty());
        assertEquals("redirect:http://eclinic.com/doctors/add/valid", redirectUrl);

        verify(doctorDAO, times(1)).save(any(DoctorEntity.class));
        verify(doctorDAO, only()).save(any(DoctorEntity.class));
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
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        // when, then
        assertThrows(BindException.class, () -> doctorController.addValidDoctor(doctorDTO, bindingResult, request));
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

        DoctorEntity doctorEntity = EntityFixtures.someDoctor5().withDoctorId(doctorId);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "/doctors/panel");

        when(doctorDAO.findEntityById(updateDoctor.getDoctorId())).thenReturn(doctorEntity);

        // when
        String viewName = doctorController.updateDoctor(updateDoctor, request);

        // then
        assertEquals("redirect:/doctors/panel", viewName);
        assertEquals(updateDoctor.getDoctorId(), doctorEntity.getDoctorId());
        assertEquals(updateDoctor.getSurname(), doctorEntity.getSurname());
        assertEquals(updateDoctor.getEmail(), doctorEntity.getEmail());

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(doctorDAO, times(1)).save(doctorEntity);
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void testDeleteDoctorById() {
        // given
        int doctorId = 1;
        DoctorEntity doctorEntity = EntityFixtures.someDoctor1();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "/doctors/panel");

        when(doctorDAO.findEntityById(doctorId)).thenReturn(doctorEntity);

        // when
        String viewName = doctorController.deleteDoctorById(doctorId, request);

        // then
        assertEquals("redirect:/doctors/panel", viewName);

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(doctorDAO, times(1)).delete(doctorEntity);
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void testDeleteDoctorById_ThrowsEntityNotFoundException() {
        // given
        int doctorId = -12;

        when(doctorDAO.findEntityById(doctorId)).thenReturn(null);

        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        assertThrows(EntityNotFoundException.class, () -> doctorController.deleteDoctorById(doctorId, request));

        // then
        verify(doctorDAO, never()).delete(any());
        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(doctorDAO, only()).findEntityById(doctorId);
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

        verify(doctorDAO, times(1)).findBySpecialization(specialization);
        verify(doctorDAO, only()).findBySpecialization(specialization);
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

        verify(doctorDAO, times(1)).findAll();
        verify(doctorDAO, only()).findAll();
    }

    @Test
    void testShowDoctorDetails() {
        // given
        int doctorId = 1;
        DoctorDTO mockDoctor = DtoFixtures.someDoctor1();
        List<OpinionDTO> opinions = Arrays.asList(DtoFixtures.someOpinion1(), DtoFixtures.someOpinion2().withDoctorId(doctorId));

        ExtendedModelMap model = new ExtendedModelMap();
        List<WorkingHours> mockWorkingHours = Arrays.asList(
                WorkingHoursFixtures.mondayHours(), WorkingHoursFixtures.fridayHours());
        String username = "testUser";
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null);

        when(doctorDAO.findById(doctorId)).thenReturn(mockDoctor);
        when(opinionDAO.findAllByDoctor(doctorId)).thenReturn(opinions);
        when(doctorService.getWorkingHours(doctorId)).thenReturn(mockWorkingHours);

        // given
        String viewName = doctorController.showDoctorDetails(doctorId, model, authentication);

        // then
        assertEquals("doctor/doctor-show", viewName);
        assertEquals(mockDoctor, model.getAttribute("doctor"));
        assertEquals(mockWorkingHours, model.getAttribute("workingHours"));
        assertEquals(username, model.getAttribute("username"));
        assertEquals(opinions, model.getAttribute("opinions"));

        verify(doctorDAO, times(1)).findById(doctorId);
        verify(opinionDAO, times(1)).findAllByDoctor(doctorId);
        verify(doctorService, times(1)).getWorkingHours(doctorId);
        verifyNoMoreInteractions(doctorDAO, opinionDAO, doctorService);
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
        assertEquals("doctor/doctors-all", result);

        verify(doctorDAO, times(1)).findAll();
        verify(doctorDAO, only()).findAll();
    }
}