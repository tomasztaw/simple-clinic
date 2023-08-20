package pl.taw.api.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.DoctorsDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.api.dto.VisitsDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.taw.api.controller.rest.DoctorRestController.*;

@WebMvcTest(controllers = DoctorRestController.class)
@AutoConfigureMockMvc(addFilters = false) // wyłączenie filtrów security dla testów Mock Mvc
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DoctorRestControllerWebMvcTest {

    @MockBean
    private DoctorDAO doctorDAO;

    @MockBean
    private VisitService visitService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @Test
    void shouldReturnsAllDoctors() throws Exception {
        // given
        DoctorsDTO doctors = DoctorsDTO.of(DtoFixtures.doctors);

        when(doctorDAO.findAll()).thenReturn(doctors.getDoctors());

        // when, then
        mockMvc.perform(get(API_DOCTORS))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors[0].name", is(doctors.getDoctors().get(0).getName())))
                .andExpect(jsonPath("$.doctors[0].surname", is(doctors.getDoctors().get(0).getSurname())))
                .andExpect(jsonPath("$.doctors[0].title", is(doctors.getDoctors().get(0).getTitle())))
                .andExpect(jsonPath("$.doctors[0].phone", is(doctors.getDoctors().get(0).getPhone())))
                .andExpect(jsonPath("$.doctors[0].email", is(doctors.getDoctors().get(0).getEmail())))
                .andExpect(jsonPath("$.doctors[0].email", containsString("@eclinic.pl")))
                .andExpect(jsonPath("$.doctors", hasSize(doctors.getDoctors().size())));

        verify(doctorDAO, only()).findAll();
    }

    @Test
    void shouldReturnDoctorDetails() throws Exception {
        // given
        Integer doctorId = 3;
        DoctorDTO existingDoctor = DtoFixtures.someDoctor3();

        when(doctorDAO.findById(doctorId)).thenReturn(existingDoctor);

        // when, then
        mockMvc.perform(get(API_DOCTORS.concat(DOCTOR_ID), doctorId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorId", is(existingDoctor.getDoctorId())))
                .andExpect(jsonPath("$.name", is(existingDoctor.getName())))
                .andExpect(jsonPath("$.surname", is(existingDoctor.getSurname())))
                .andExpect(jsonPath("$.title", is(existingDoctor.getTitle())))
                .andExpect(jsonPath("$.email", is(existingDoctor.getEmail())))
                .andExpect(jsonPath("$.phone", is(existingDoctor.getPhone())));

        verify(doctorDAO, only()).findById(doctorId);
    }

    @Test
    void shouldReturnAllHistoryForDoctor() throws Exception {
        // given
        Integer doctorId = 2;
        DoctorDTO doctor = DtoFixtures.someDoctor2();
        List<VisitDTO> visitList = DtoFixtures.visits.stream().map(visit -> visit.withDoctorId(doctorId)).toList();
        VisitsDTO visits = VisitsDTO.of(visitList);

        when(doctorDAO.findById(doctorId)).thenReturn(doctor);
        when(visitService.findAllVisitByDoctor(doctorId)).thenReturn(visits.getVisits());

        // when, then
        mockMvc.perform(get(API_DOCTORS.concat(HISTORY), doctorId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visits[*].doctorId", everyItem(is(doctor.getDoctorId()))))
                .andExpect(jsonPath("$.visits[*].doctor.name", everyItem(is(doctor.getName()))));

        verify(visitService, times(1)).findAllVisitByDoctor(doctorId);
        verify(doctorDAO, times(1)).findById(doctorId);
        verifyNoMoreInteractions(visitService, doctorDAO);
    }

    @Test
    void addDoctorShouldWorksCorrectly() throws Exception {
        // given
        DoctorDTO doctor = DtoFixtures.someDoctor3();
        DoctorEntity doctorEntity = EntityFixtures.someDoctor3();

        when(doctorDAO.saveAndReturn(any(DoctorEntity.class))).thenReturn(doctorEntity);

        // when, then
        mockMvc.perform(post(API_DOCTORS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctor))
                ).andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/doctors/%s".formatted(doctor.getDoctorId())));

        verify(doctorDAO, times(1)).saveAndReturn(any(DoctorEntity.class));
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void updateDoctorShouldWorksCorrectly() throws Exception {
        // given
        Integer doctorId = 3;
        DoctorDTO doctorDTO = DtoFixtures.someDoctor3();
        DoctorEntity doctorEntity = EntityFixtures.someDoctor3();

        when(doctorDAO.findEntityById(doctorId)).thenReturn(doctorEntity);

        // when, then
        mockMvc.perform(put(API_DOCTORS.concat(DOCTOR_ID_RESULT).formatted(doctorId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorDTO)))
                .andExpect(status().isOk());

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(doctorDAO, times(1)).save(doctorEntity);
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void shouldDeleteExistingDoctorAndReturnNoContent() throws Exception {
        // given
        Integer doctorId = 1;
        DoctorEntity existingDoctor = EntityFixtures.someDoctor1();

        when(doctorDAO.findEntityById(doctorId)).thenReturn(existingDoctor);

        // when, then
        mockMvc.perform(delete(API_DOCTORS.concat(DOCTOR_ID_RESULT.formatted(doctorId))))
                .andExpect(status().isNoContent());

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(doctorDAO, times(1)).delete(existingDoctor);
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void shouldReturnNotFoundForNonExistingDoctor() throws Exception {
        // given
        Integer doctorId = 1;

        when(doctorDAO.findEntityById(doctorId)).thenReturn(null);

        // when, then
        mockMvc.perform(delete(API_DOCTORS.concat(DOCTOR_ID_RESULT.formatted(doctorId))))
                .andExpect(status().isNotFound());

        verify(doctorDAO, only()).findEntityById(doctorId);
        verify(doctorDAO, never()).delete(any());
    }

    @Test
    void updateDoctorTitleShouldWorksCorrectly() throws Exception {
        // given
        Integer doctorId = 2;
        String newTitle = "Najlepszy z najlepszych";
        DoctorEntity existingDoctor = EntityFixtures.someDoctor2();

        when(doctorDAO.findEntityById(doctorId)).thenReturn(existingDoctor);

        // when, then
        mockMvc.perform(patch(String.format("%s/%d/title", API_DOCTORS, doctorId))
                        .param("newTitle", newTitle))
                .andExpect(status().isOk());

        assertEquals(newTitle, existingDoctor.getTitle());

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(doctorDAO, times(1)).save(existingDoctor);
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void shouldUpdateDoctorPhoneAndReturnOk() throws Exception {
        // given
        Integer doctorId = 1;
        String newPhone = "+48 987 654 321";

        DoctorEntity existingDoctor = EntityFixtures.someDoctor1();

        when(doctorDAO.findEntityById(doctorId)).thenReturn(existingDoctor);

        // when, then
        mockMvc.perform(patch(String.format("%s/%d/phone", API_DOCTORS, doctorId))
                        .param("newPhone", newPhone))
                .andExpect(status().isOk());

        assertEquals(newPhone, existingDoctor.getPhone());

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(doctorDAO, times(1)).save(existingDoctor);
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void thatEmailValidationWorksCorrectly() throws Exception {
        // given
        final var request = """
                {
                    "email": "jakis@zly@email.com"
                }
                """;

        // when, then
        mockMvc.perform(
                        post(API_DOCTORS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Niepoprawne żądanie dla pola: [email]")));
        verifyNoInteractions(doctorDAO);
    }

    @ParameterizedTest
    @MethodSource
    void thatPhoneValidationWorksCorrectly(Boolean correctPhone, String phone) throws Exception {
        // given
        Integer doctorId = 123;
        final var request = """
                {
                    "phone": "%s"
                }
                """.formatted(phone);

        when(doctorDAO.saveAndReturn(any(DoctorEntity.class)))
                .thenReturn(EntityFixtures.someDoctor1().withDoctorId(doctorId));

        // when, then
        if (correctPhone) {
            String expectedRedirect = API_DOCTORS.concat(DOCTOR_ID_RESULT).formatted(doctorId);
            mockMvc.perform(post(API_DOCTORS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isCreated())
                    .andExpect(redirectedUrl(expectedRedirect));
            verify(doctorDAO, only()).saveAndReturn(any(DoctorEntity.class));
        } else {
            mockMvc.perform(post(API_DOCTORS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Niepoprawne żądanie dla pola: [phone]")));
            verifyNoInteractions(doctorDAO);
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