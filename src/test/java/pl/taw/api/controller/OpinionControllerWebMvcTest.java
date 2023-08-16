package pl.taw.api.controller;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.OpinionEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.taw.api.controller.OpinionController.*;


@WebMvcTest(controllers = OpinionController.class)
@AutoConfigureMockMvc(addFilters = false) // wyłączenie filtrów security dla testów Mock Mvc
@AllArgsConstructor(onConstructor = @__(@Autowired))
class OpinionControllerWebMvcTest {

    private final MockMvc mockMvc;

    @MockBean
    private final OpinionDAO opinionDAO;
    @MockBean
    private final VisitDAO visitDAO;
    @MockBean
    private final DoctorDAO doctorDAO;
    @MockBean
    private final PatientDAO patientDAO;
    @MockBean
    private final Authentication authentication;

    @Test
    public void testShowDoctorOpinions() throws Exception {
        // given
        Integer doctorId = 1;
        DoctorDTO doctor = DtoFixtures.someDoctor1();
        List<OpinionDTO> opinions = DtoFixtures.opinions.stream().map(opinion -> opinion.withDoctorId(doctorId)).toList();

        when(doctorDAO.findById(doctorId)).thenReturn(doctor);
        when(opinionDAO.findAllByDoctor(doctorId)).thenReturn(opinions);

        // when, then
        mockMvc.perform(get(OPINIONS.concat(DOCTOR_ID), doctorId))
                .andExpect(status().isOk())
                .andExpect(view().name("opinion/opinion-doctor-all"))
                .andExpect(model().attribute("opinions", opinions))
                .andExpect(model().attribute("doctor", doctor));

        verify(doctorDAO, times(1)).findById(doctorId);
        verify(opinionDAO, times(1)).findAllByDoctor(doctorId);
    }

    @Test
    public void testShowPatientOpinions() throws Exception {
        // given
        Integer patientId = 2;
        PatientDTO patient = DtoFixtures.somePatient2();
        List<OpinionDTO> opinions = DtoFixtures.opinions.stream().map(opinion -> opinion.withPatientId(patientId)).toList();

        when(patientDAO.findById(patientId)).thenReturn(patient);
        when(opinionDAO.findAllByPatient(patientId)).thenReturn(opinions);

        // when, then
        mockMvc.perform(get(OPINIONS.concat(PATIENT_ID), patientId))
                .andExpect(status().isOk())
                .andExpect(view().name("opinion/opinion-patient-all"))
                .andExpect(model().attribute("opinions", opinions))
                .andExpect(model().attribute("patient", patient));

        verify(patientDAO, times(1)).findById(patientId);
        verify(opinionDAO, times(1)).findAllByPatient(patientId);
    }

    @Test
    public void testShowOpinionPanel() throws Exception {
        // given
        List<OpinionDTO> opinions = DtoFixtures.opinions;
        String username = authentication.getName();

        when(opinionDAO.findAll()).thenReturn(opinions);

        // when, then
        mockMvc.perform(get(
                OPINIONS.concat(PANEL))
                        .with(user("testUser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("opinion/opinion-panel"))
                .andExpect(model().attribute("opinions", opinions))
                .andExpect(model().attributeExists("updateOpinion"))
                .andExpect(model().attribute("username", username));

        verify(opinionDAO, times(1)).findAll();
    }

    @Test
    public void testShowOpinion() throws Exception {
        // given
        Integer opinionId = 1;
        OpinionDTO opinion = DtoFixtures.someOpinion1();
        VisitDTO visit = DtoFixtures.someVisit1();

        when(opinionDAO.findById(opinionId)).thenReturn(opinion);
        when(visitDAO.findById(opinion.getVisitId())).thenReturn(visit);

        // when, then
        mockMvc.perform(get(
                OPINIONS.concat(SHOW),
                        opinionId))
                .andExpect(status().isOk())
                .andExpect(view().name("opinion/opinion-show"))
                .andExpect(model().attribute("opinion", opinion))
                .andExpect(model().attribute("visit", visit));

        verify(opinionDAO, times(1)).findById(opinionId);
        verify(visitDAO, times(1)).findById(opinion.getVisitId());
    }

    @Test
    public void testAddOpinion() throws Exception {
        // given
        Integer doctorId = 1, patientId = 2, visitId = 3;
        String comment = "To jest jakakolwiek opinia";

        // when, then
        mockMvc.perform(post(OPINIONS.concat(ADD))
                        .param("doctorId", doctorId.toString())
                        .param("patientId", patientId.toString())
                        .param("visitId", visitId.toString())
                        .param("comment", comment)
                        .param("createdAt", LocalDateTime.now().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(opinionDAO, times(1)).save(any(OpinionEntity.class));
    }

    @Test
    public void testAddOpinionFromVisit() throws Exception {
        // given
        Integer visitId = 1, doctorId = 2 ,patientId = 3;
        String comment = "Ta opinia, to jest świ...";

        // when, then
        mockMvc.perform(post(OPINIONS.concat(ADD_FROM_VISIT),
                        visitId, doctorId, patientId)
                        .param("comment", comment)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(opinionDAO, times(1)).save(any(OpinionEntity.class));
    }

    @Test
    public void testUpdateOpinion() throws Exception {
        // given
        OpinionDTO updateOpinion = DtoFixtures.someOpinion1();
        OpinionEntity existingOpinion = EntityFixtures.someOpinion1();

        when(opinionDAO.findEntityById(updateOpinion.getOpinionId())).thenReturn(existingOpinion); // Możesz dostosować atrapę odpowiednio

        // when, then
        mockMvc.perform(put(OPINIONS.concat(UPDATE))
                        .flashAttr("updateOpinion", updateOpinion)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(visitDAO, times(1)).findEntityById(updateOpinion.getOpinionId());
        verify(opinionDAO, times(1)).save(existingOpinion);
    }

    @Test
    public void testUpdateOpinionById() throws Exception {
        // given
        Integer opinionId = 1;
        String newComment = "Opinia jak każda inna";

        OpinionEntity opinionEntity = EntityFixtures.someOpinion1();
        when(opinionDAO.findEntityById(opinionId)).thenReturn(opinionEntity);

        // when, then
        mockMvc.perform(put(OPINIONS.concat(UPDATE_BY_ID), opinionId)
                        .param("newComment", newComment)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        assertEquals(newComment, opinionEntity.getComment());

        verify(opinionDAO, times(1)).findEntityById(opinionId);
        verify(opinionDAO, times(1)).save(opinionEntity);
        verifyNoMoreInteractions(opinionDAO);
    }

    @Test
    public void testDeleteOpinionById() throws Exception {
        // given
        Integer opinionId = 1;
        OpinionEntity opinionEntity = EntityFixtures.someOpinion1().withVisitId(5).withVisit(EntityFixtures.someVisit5());
        VisitEntity visitEntity = EntityFixtures.someVisit5().withDoctorId(opinionEntity.getDoctorId()).withPatientId(opinionEntity.getPatientId());

        when(opinionDAO.findEntityById(opinionId)).thenReturn(opinionEntity);

        // when, then
        mockMvc.perform(delete(OPINIONS.concat(DELETE), opinionId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(opinionDAO, times(1)).findEntityById(opinionId);
        verify(visitDAO, times(1)).save(visitEntity);
        verify(opinionDAO, times(1)).delete(opinionEntity);

        verifyNoMoreInteractions(opinionDAO, visitDAO);
    }

}