package pl.taw.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpinionControllerMockitoTest {

    @Mock
    private OpinionDAO opinionDAO;
    @Mock
    private VisitDAO visitDAO;
    @Mock
    private DoctorDAO doctorDAO;
    @Mock
    private PatientDAO patientDAO;
    @Mock
    private Model model;
    @Mock
    private Authentication authentication;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private OpinionController opinionController;

    @Test
    public void testShowDoctorOpinions() {
        // given
        Integer doctorId = 1;
        List<OpinionDTO> opinions = DtoFixtures.opinions;
        DoctorDTO doctorDTO = DtoFixtures.someDoctor1();

        when(opinionDAO.findAllByDoctor(doctorId)).thenReturn(opinions);
        when(doctorDAO.findById(doctorId)).thenReturn(doctorDTO);

        // when
        String viewName = opinionController.showDoctorOpinions(doctorId, model);

        // then
        assertEquals("opinion/opinion-doctor-all", viewName);
        verify(opinionDAO, times(1)).findAllByDoctor(doctorId);
        verify(doctorDAO, times(1)).findById(doctorId);
        verify(model).addAttribute("opinions", opinions);
        verify(model).addAttribute("doctor", doctorDTO);
    }

    @Test
    public void testShowPatientOpinions() {
        // given
        Integer patientId = 1;
        List<OpinionDTO> opinions = DtoFixtures.opinions;
        PatientDTO patientDTO = DtoFixtures.somePatient1();

        when(opinionDAO.findAllByPatient(patientId)).thenReturn(opinions);
        when(patientDAO.findById(patientId)).thenReturn(patientDTO);

        // when
        String viewName = opinionController.showPatientOpinions(patientId, model);

        // then
        assertEquals("opinion/opinion-patient-all", viewName);
        verify(opinionDAO, times(1)).findAllByPatient(patientId);
        verify(patientDAO, times(1)).findById(patientId);
        verify(model).addAttribute("opinions", opinions);
        verify(model).addAttribute("patient", patientDTO);
    }

    @Test
    public void testShowOpinionPanel() {
        // given
        List<OpinionDTO> opinions = DtoFixtures.opinions;

        when(opinionDAO.findAll()).thenReturn(opinions);
        when(authentication.getName()).thenReturn("testUser");

        // when
        String viewName = opinionController.showOpinionPanel(model, authentication);

        // then
        assertEquals("opinion/opinion-panel", viewName);
        verify(opinionDAO, times(1)).findAll();
        verify(authentication, times(1)).getName();
        verify(model).addAttribute("opinions", opinions);
        verify(model).addAttribute(eq("updateOpinion"), any(OpinionDTO.class));
        verify(model).addAttribute("username", "testUser");
    }

    @Test
    public void testShowOpinion() {
        // given
        Integer opinionId = 2;
        OpinionDTO opinion = DtoFixtures.someOpinion2();
        VisitDTO visit = DtoFixtures.someVisit2();

        when(opinionDAO.findById(opinionId)).thenReturn(opinion);
        when(visitDAO.findById(opinion.getVisitId())).thenReturn(visit);

        // when
        String viewName = opinionController.showOpinion(opinionId, model);

        // then
        assertEquals("opinion/opinion-show", viewName);
        verify(opinionDAO, times(1)).findById(opinionId);
        verify(visitDAO, times(1)).findById(opinion.getVisitId());
        verify(model).addAttribute("opinion", opinion);
        verify(model).addAttribute("visit", visit);
    }

    @Test
    public void testAddOpinion() {
        // given
        Integer doctorId = 1;
        Integer patientId = 2;
        Integer visitId = 3;
        String comment = "Opinia testowa";
        LocalDateTime createdAt = LocalDateTime.of(LocalDate.of(2023, 8, 8), LocalTime.of(10, 10, 0));

        VisitEntity visitEntity = EntityFixtures.someVisit3();

        when(visitDAO.findEntityById(visitId)).thenReturn(visitEntity);
        when(request.getHeader("Referer")).thenReturn("mockReferer");

        // when
        String result = opinionController.addOpinion(doctorId, patientId, visitId, comment, createdAt, request);

        // then
        assertEquals("redirect:mockReferer", result);
        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(opinionDAO, times(1)).save(any(OpinionEntity.class));
        verify(request, times(1)).getHeader("Referer");
    }

    @Test
    public void testAddOpinionFromVisit() {
        // given
        Integer visitId = 1;
        Integer doctorId = 2;
        Integer patientId = 3;
        String comment = "Opinia jak opinia";

        when(request.getHeader("Referer")).thenReturn("mockReferer");

        // when
        String result = opinionController.addOpinionFromVisit(visitId, doctorId, patientId, comment, request);

        // then
        assertEquals("redirect:mockReferer", result);
        verify(opinionDAO, times(1)).save(any(OpinionEntity.class));
        verify(request, times(1)).getHeader("Referer");

    }

    @Test
    public void testUpdateOpinion() {
        // given
        OpinionDTO updateOpinion = DtoFixtures.someOpinion1();
        VisitEntity visit = EntityFixtures.someVisit1();
        OpinionEntity opinionEntity = EntityFixtures.someOpinion2();
        String referer = request.getHeader("Referer");
        String expectedRedirect = "redirect:" + referer;

        when(visitDAO.findEntityById(updateOpinion.getOpinionId())).thenReturn(visit);
        when(opinionDAO.findEntityById(updateOpinion.getOpinionId())).thenReturn(opinionEntity);

        // when
        String result = opinionController.updateOpinion(updateOpinion, request);

        // then
        assertEquals(expectedRedirect, result);

        verify(opinionDAO, times(1)).save(opinionEntity);
        verify(opinionDAO, times(1)).findEntityById(updateOpinion.getOpinionId());
        verify(visitDAO, times(1)).findEntityById(updateOpinion.getOpinionId());
    }

    @Test
    public void testUpdateOpinionById() {
        // given
        Integer opinionId = 2;
        String newComment = "Nowa opinia na temat wizyty...";
        OpinionEntity opinionEntity = EntityFixtures.someOpinion2();
        String referer = request.getHeader("Referer");
        String expectedRedirect = "redirect:" + referer;

        when(opinionDAO.findEntityById(opinionId)).thenReturn(opinionEntity);

        // when
        String result = opinionController.updateOpinionById(opinionId, newComment, request);

        // then
        assertEquals(expectedRedirect, result);

        verify(opinionDAO, times(1)).save(opinionEntity);
        verify(opinionDAO, times(1)).findEntityById(opinionId);
    }

    @Test
    public void testDeleteOpinionById() {
        // given
        Integer opinionId = 1;
        OpinionEntity opinionEntity = EntityFixtures.someOpinion1();
        String referer = request.getHeader("Referer");
        String expectedRedirect = "redirect:" + referer;

        when(opinionDAO.findEntityById(opinionId)).thenReturn(opinionEntity);

        // when
        String result = opinionController.deleteOpinionById(opinionId, request);

        // then
        assertEquals(expectedRedirect, result);
        verify(opinionDAO, times(1)).delete(opinionEntity);
    }
}