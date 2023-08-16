package pl.taw.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitControllerMockitoTest {

    @Mock
    private VisitDAO visitDAO;
    @Mock
    private VisitService visitService;
    @Mock
    private DoctorDAO doctorDAO;
    @Mock
    private PatientDAO patientDAO;
    @Mock
    private OpinionDAO opinionDAO;
    @Mock
    private Model model;
    @Mock
    private Authentication authentication;
    @Mock
    private HttpServletRequest request;


    @InjectMocks
    private VisitController visitController;

    @Test
    public void testGetVisitById() {
        // given
        Integer visitId = 1;
        Integer wrongId = -5;
        VisitDTO visitDTO = DtoFixtures.someVisit1();

        when(visitDAO.findById(visitId)).thenReturn(visitDTO);
        when(visitDAO.findById(wrongId)).thenReturn(null);

        // when
        ResponseEntity<?> responseFound = visitController.getVisitById(visitId);
        ResponseEntity<?> responseNotFound = visitController.getVisitById(wrongId);

        // then
        assertEquals(HttpStatus.OK, responseFound.getStatusCode());
        assertSame(visitDTO, responseFound.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseNotFound.getStatusCode());

        verify(visitDAO, times(1)).findById(visitId);
    }

    @Test
    public void testVisitsDoctorAndPatient() {
        // given
        Integer doctorId = 1, patientId = 2;
        List<VisitDTO> visits = DtoFixtures.visits;
        DoctorDTO doctor = DtoFixtures.someDoctor1();
        PatientDTO patient = DtoFixtures.somePatient2();
        String username = "testUser";

        when(visitDAO.findAllForBoth(anyInt(), anyInt())).thenReturn(visits);
        when(doctorDAO.findById(anyInt())).thenReturn(doctor);
        when(patientDAO.findById(anyInt())).thenReturn(patient);
        when(authentication.getName()).thenReturn(username);

        // when
        String result = visitController.visitsDoctorAndPatient(doctorId, patientId, authentication, model);

        // then
        assertEquals("visit/doctor-and-patient", result);

        verify(visitDAO, times(1)).findAllForBoth(doctorId, patientId);
        verify(doctorDAO, times(1)).findById(doctorId);
        verify(patientDAO, times(1)).findById(patientId);
        verify(authentication, times(1)).getName();

        verify(model, times(1)).addAttribute("username", username);
        verify(model, times(1)).addAttribute("visits", visits);
        verify(model, times(1)).addAttribute("doctor", doctor);
        verify(model, times(1)).addAttribute("patient", patient);
    }

    @Test
    public void testGetPaginationVisits() {
        // given
        List<VisitDTO> visits = DtoFixtures.visits;
        Page<VisitDTO> visitsPage = new PageImpl<>(visits);
        int currentPage = 1;
        int totalPages = 1;
        int selectedSize = 10;

        when(visitService.getVisitsPage(currentPage, selectedSize)).thenReturn(visitsPage);

        // when
        String result = visitController.getPaginationVisits(currentPage, selectedSize, authentication, model);

        // then
        assertEquals("visit/visit-pagination", result);

        verify(visitService, times(1)).getVisitsPage(currentPage, selectedSize);

        verify(model, times(1)).addAttribute("visits", visits);
        verify(model, times(1)).addAttribute("currentPage", currentPage);
        verify(model, times(1)).addAttribute("totalPages", totalPages);
        verify(model, times(1)).addAttribute("selectedSize", selectedSize);
    }

    @Test
    public void testShowVisitPanel() {
        // given
        List<VisitDTO> visits = DtoFixtures.visits;

        when(visitDAO.findAll()).thenReturn(visits);

        // when
        String result = visitController.showVisitPanel(model, authentication);

        // then
        assertEquals("visit/visit-panel", result);

        verify(visitDAO, times(1)).findAll();
        verify(authentication, times(1)).getName();

        verify(model, times(1)).addAttribute("visits", visits);
        verify(model, times(1)).addAttribute("updateVisit", new VisitEntity());
        verify(model, times(1)).addAttribute("username", authentication.getName());
    }

    @Test
    public void testShowVisit() {
        // given
        Integer visitId = 2;
        OpinionDTO opinionDTO = DtoFixtures.someOpinion2().withVisitId(visitId);
        VisitDTO visitDTO = DtoFixtures.someVisit2().withOpinion(opinionDTO);

        when(visitDAO.findById(visitId)).thenReturn(visitDTO);
        when(opinionDAO.findById(visitDTO.getOpinion().getOpinionId())).thenReturn(opinionDTO);

        // when
        String viewName = visitController.showVisit(visitId, model);

        // then
        assertEquals("visit/visit-show", viewName);

        verify(visitDAO, times(1)).findById(visitId);
        verify(opinionDAO, times(1)).findById(visitDTO.getOpinion().getOpinionId());

        verify(model).addAttribute("visit", visitDTO);
        verify(model).addAttribute("opinion", opinionDTO);
    }

    @Test
    public void testShowVisitsByDoctor() {
        // given
        Integer doctorId = 3;
        List<VisitDTO> visits = DtoFixtures.visits.stream().map(visit -> visit.withDoctorId(doctorId)).toList();
        DoctorDTO doctorDTO = DtoFixtures.someDoctor3();

        when(visitService.findAllVisitByDoctor(doctorId)).thenReturn(visits);
        when(doctorDAO.findById(doctorId)).thenReturn(doctorDTO);

        // when
        String viewName = visitController.showVisitsByDoctor(doctorId, model);

        // then
        assertEquals("doctor/doctor-visits", viewName);

        verify(visitService, times(1)).findAllVisitByDoctor(doctorId);
        verify(doctorDAO, times(1)).findById(doctorId);

        verify(model).addAttribute("visits", visits);
        verify(model).addAttribute("doctor", doctorDTO);
    }

    @Test
    public void testShowVisitsByPatient() {
        // given
        Integer patientId = 2;
        List<VisitDTO> visits = DtoFixtures.visits.stream().map(visit -> visit.withPatientId(patientId)).toList();
        PatientDTO patientDTO = DtoFixtures.somePatient2();

        when(visitService.findAllVisitByPatient(patientId)).thenReturn(visits);
        when(patientDAO.findById(patientId)).thenReturn(patientDTO);

        // when
        String viewName = visitController.showVisitsByPatient(patientId, model);

        // then
        assertEquals("patient/patient-history", viewName);

        verify(visitService, times(1)).findAllVisitByPatient(patientId);
        verify(patientDAO, times(1)).findById(patientId);

        verify(model).addAttribute("visits", visits);
        verify(model).addAttribute("patient", patientDTO);
    }

    @Test
    public void testAddOpinionsForVisits() {
        // given
        Integer patientId = 1;
        List<VisitDTO> visits = DtoFixtures.visits;
        PatientDTO patientDTO = DtoFixtures.somePatient1();

        when(visitService.findAllVisitByPatient(patientId)).thenReturn(visits);
        when(patientDAO.findById(patientId)).thenReturn(patientDTO);

        // when
        String viewName = visitController.addOpinionsForVisits(patientId, model);

        // then
        assertEquals("opinion/opinion-to-add", viewName);

        verify(visitService, times(1)).findAllVisitByPatient(patientId);
        verify(patientDAO, times(1)).findById(patientId);

        verify(model).addAttribute("visits", visits);
        verify(model).addAttribute("patient", patientDTO);
    }

    @Test
    public void testAddVisit() {
        // given
        Integer doctorId = 1;
        Integer patientId = 2;
        LocalDateTime dateTime = LocalDateTime.of(2023,8,8,12,50);
        String note = "Notatka testowa";
        String status = "odbyta";
        String referer = request.getHeader("Referer");
        String expectedRedirect = "redirect:" + referer;

        // when
        String redirectUrl = visitController.addVisit(doctorId, patientId, dateTime, note, status, request);

        // then
        assertEquals("redirect:null", redirectUrl);
        assertEquals(expectedRedirect, redirectUrl);

        verify(visitDAO, times(1)).save(any(VisitEntity.class));
    }

    @Test
    public void testGenerateVisit() {
        // given
        Integer doctorId = 1;
        Integer patientId = 2;
        String date = "2023-08-07";
        String time = "14:30";
        String note = "Tragedia";
        String referer = request.getHeader("Referer");
        String expectedRedirect = "redirect:" + referer;

        // when
        String redirectUrl = visitController.generateVisit(doctorId, patientId, date, time, note, request);

        // then
        assertEquals("redirect:null", redirectUrl);
        assertEquals(expectedRedirect, redirectUrl);

        verify(visitDAO, times(1)).save(any(VisitEntity.class));
    }

    @Test
    public void testUpdateVisitById() {
        // given
        Integer visitId = 1;
        Integer doctorId = 2;
        Integer patientId = 3;
        LocalDateTime dateTime = LocalDateTime.of(2023, 8, 8, 10, 20);
        String note = "Nowa notatka";
        String status = "odbyta";

        DoctorEntity doctorEntity = EntityFixtures.someDoctor2();
        PatientEntity patientEntity = EntityFixtures.somePatient3();
        VisitEntity visitEntity = EntityFixtures.someVisit1();

        when(doctorDAO.findEntityById(doctorId)).thenReturn(doctorEntity);
        when(patientDAO.findEntityById(patientId)).thenReturn(patientEntity);
        when(visitDAO.findEntityById(visitId)).thenReturn(visitEntity);

        // when
        String redirectUrl = visitController.updateVisitById(visitId, doctorId, patientId, dateTime, note, status, request);

        // then
        assertEquals("redirect:null", redirectUrl);

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(patientDAO, times(1)).findEntityById(patientId);
        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).save(visitEntity);
    }

    @Test
    public void testUpdateVisit() {
        // given
        Integer doctorId = 3, patientId = 3, visitId = 3;
        DoctorEntity doctorEntity = EntityFixtures.someDoctor3();
        PatientEntity patientEntity = EntityFixtures.somePatient3();
        VisitEntity visitEntity = EntityFixtures.someVisit3();
        VisitEntity updateVisit = EntityFixtures.someVisit3();

        when(doctorDAO.findEntityById(doctorId)).thenReturn(doctorEntity);
        when(patientDAO.findEntityById(patientId)).thenReturn(patientEntity);
        when(visitDAO.findEntityById(visitId)).thenReturn(visitEntity);

        // when
        String redirectUrl = visitController.updateVisit(updateVisit, request);

        // then
        assertEquals("redirect:null", redirectUrl);

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(patientDAO, times(1)).findEntityById(patientId);
        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).save(visitEntity);
    }

    @Test
    public void testUpdateVisitNote() {
        // given
        Integer visitId = 1;
        String newNote = "Nowa notatka";
        String referer = request.getHeader("Referer");
        String expectedRedirect = "redirect:" + referer;
        VisitEntity visitEntity = EntityFixtures.someVisit1();

        when(visitDAO.findEntityById(visitId)).thenReturn(visitEntity);

        // when
        String result = visitController.updateVisitNote(visitId, newNote, request);

        // then
        assertEquals(expectedRedirect, result);

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).save(visitEntity);
    }

    @Test
    public void testSimpleDeleteVisitById() {
        // given
        Integer visitId = 2;
        String referer = "http://example.com";
        VisitEntity visitEntity = EntityFixtures.someVisit2();

        when(visitDAO.findEntityById(visitId)).thenReturn(visitEntity);
        when(request.getHeader("Referer")).thenReturn(referer);

        // when
        String result = visitController.simpleDeleteVisitById(visitId, request);

        // then
        assertEquals("redirect:" + referer, result);

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).delete(visitEntity);
        verify(request, times(1)).getHeader("Referer");
    }

    @Test
    public void testDeleteVisitById_Success() throws URISyntaxException {
        // given
        Integer visitId = 1;
        String referer = "http://example.com";
        VisitEntity visitEntity = EntityFixtures.someVisit1();

        when(visitDAO.findEntityById(visitId)).thenReturn(visitEntity);
        when(request.getHeader("Referer")).thenReturn(referer);

        // when
        ResponseEntity<String> response = visitController.deleteVisitById(visitId, request);

        // then
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        assertEquals(new URI(referer), response.getHeaders().getLocation());

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).delete(visitEntity);
        verify(request, times(1)).getHeader("Referer");
    }

    @Test
    public void testDeleteVisitById_NotFound() {
        // given
        Integer visitId = 1;

        when(visitDAO.findEntityById(visitId)).thenReturn(null);

        // when
        ResponseEntity<String> response = visitController.deleteVisitById(visitId, request);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, never()).delete(any());
    }

    @Test
    public void testDeleteVisitById_UriSyntaxError() {
        // given
        Integer visitId = 5;
        String referer = "invalid-url";
        VisitEntity visitEntity = EntityFixtures.someVisit5();

        when(visitDAO.findEntityById(visitId)).thenReturn(visitEntity);
        when(request.getHeader("Referer")).thenReturn(referer);

        // when
        ResponseEntity<String> response = visitController.deleteVisitById(visitId, request);

        // then
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).delete(visitEntity);
        verify(request, times(1)).getHeader("Referer");
    }

}