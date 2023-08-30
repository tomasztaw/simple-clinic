package pl.taw.api.controller;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.taw.api.controller.VisitController.*;

@WebMvcTest(controllers = VisitController.class)
@AutoConfigureMockMvc(addFilters = false) // wyłączenie filtrów security dla testów Mock Mvc
@AllArgsConstructor(onConstructor = @__(@Autowired))
class VisitControllerWebMvcTest {

    private final MockMvc mockMvc;

    @MockBean
    private final VisitDAO visitDAO;
    @MockBean
    private final VisitService visitService;
    @MockBean
    private final DoctorDAO doctorDAO;
    @MockBean
    private final PatientDAO patientDAO;
    @MockBean
    private final OpinionDAO opinionDAO;
    @MockBean
    private final Authentication authentication;


    @Test
    public void testGetVisitById() throws Exception {
        // given
        Integer visitId = 1;
        VisitDTO visitDTO = DtoFixtures.someVisit1();

        when(visitDAO.findById(visitId)).thenReturn(visitDTO);

        // when, then
        mockMvc.perform(get(VISITS.concat(VISIT_ID), visitId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.doctorId").value(visitDTO.getDoctorId()))
                .andExpect(jsonPath("$.visitId").value(visitDTO.getVisitId()));

        verify(visitDAO, times(1)).findById(visitId);
    }

    @Test
    public void testVisitsDoctorAndPatient() throws Exception {
        // given
        Integer doctorId = 1, patientId = 2;
        List<VisitDTO> visits = DtoFixtures.visits.stream().map(visit -> visit.withDoctorId(doctorId).withPatientId(patientId)).toList();
        DoctorDTO doctor = DtoFixtures.someDoctor1();
        PatientDTO patient = DtoFixtures.somePatient2();

        when(visitDAO.findAllForBoth(doctorId, patientId)).thenReturn(visits);
        when(doctorDAO.findById(doctorId)).thenReturn(doctor);
        when(patientDAO.findById(patientId)).thenReturn(patient);

        // when, then
        mockMvc.perform(get(VISITS.concat(DOCTOR_AND_PATIENT), doctorId, patientId))
                .andExpect(status().isOk())
                .andExpect(view().name("visit/doctor-and-patient"))
                .andExpect(model().attribute("visits", visits))
                .andExpect(model().attribute("doctor", doctor))
                .andExpect(model().attribute("patient", patient));

        verify(visitDAO, times(1)).findAllForBoth(doctorId, patientId);
        verify(doctorDAO, times(1)).findById(doctorId);
        verify(patientDAO, times(1)).findById(patientId);
    }

    @Test
    public void testGetPaginationVisits() throws Exception {
        // given
        int page = 0, size = 5;
        DoctorDTO doctorForVisits = DtoFixtures.someDoctor1();
        PatientDTO patientForVisits = DtoFixtures.somePatient1();
        List<VisitDTO> visits = DtoFixtures.visits.stream().map(visit -> visit.withDoctor(doctorForVisits).withPatient(patientForVisits)).toList();
        Page<VisitDTO> visitsPage = new PageImpl<>(visits);

        when(visitService.getVisitsPage(page, size)).thenReturn(visitsPage);

        // when, then
        mockMvc.perform(get(VISITS.concat(PAGINATION))
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(view().name("visit/visit-pagination"))
                .andExpect(model().attribute("visits", visitsPage.getContent()))
                .andExpect(model().attribute("currentPage", page))
                .andExpect(model().attribute("totalPages", visitsPage.getTotalPages()))
                .andExpect(model().attribute("selectedSize", size));

        verify(visitService, times(1)).getVisitsPage(page, size);
    }

    @Test
    public void testShowVisitPanel() throws Exception {
        // given
        DoctorDTO doctor = DtoFixtures.someDoctor1();
        PatientDTO patient = DtoFixtures.somePatient2();
        List<VisitDTO> visits = DtoFixtures.visits.stream()
                .map(visit -> visit.withDoctorId(doctor.getDoctorId()).withDoctor(doctor)
                        .withPatientId(patient.getPatientId()).withPatient(patient)).toList();
        String username = authentication.getName();

        when(visitDAO.findAll()).thenReturn(visits);

        // when, then
        mockMvc.perform(get(VISITS.concat(PANEL)))
                .andExpect(status().isOk())
                .andExpect(view().name("visit/visit-panel"))
                .andExpect(model().attribute("visits", visits))
                .andExpect(model().attribute("username", username))
                .andExpect(model().attributeExists("updateVisit"));

        verify(visitDAO, times(1)).findAll();
    }

    @Test
    public void testShowVisit() throws Exception {
        // given
        Integer visitId = 1;
        DoctorDTO doctor = DtoFixtures.someDoctor1().withDoctorId(5);
        PatientDTO patient = DtoFixtures.somePatient1().withPatientId(5);
        OpinionDTO opinionDTO = DtoFixtures.someOpinion1();
        VisitDTO visitDTO = DtoFixtures.someVisit1().withDoctor(doctor).withPatient(patient).withOpinion(opinionDTO);

        when(visitDAO.findById(visitId)).thenReturn(visitDTO);
        when(opinionDAO.findById(any())).thenReturn(opinionDTO);

        // when, then
        mockMvc.perform(get(VISITS.concat(SHOW), visitId))
                .andExpect(status().isOk())
                .andExpect(view().name("visit/visit-show"))
                .andExpect(model().attribute("visit", visitDTO))
                .andExpect(model().attribute("opinion", opinionDTO));

        verify(visitDAO, times(1)).findById(visitId);
        verify(opinionDAO, times(1)).findById(any());
    }

    @Test
    public void testShowVisitsByDoctor() throws Exception {
        // given
        Integer doctorId = 1;
        DoctorDTO doctor = DtoFixtures.someDoctor1();
        PatientDTO patient = DtoFixtures.somePatient2();
        List<VisitDTO> visits = DtoFixtures.visits.stream().map(visit -> visit.withDoctorId(doctorId).withDoctor(doctor)
                .withPatientId(patient.getPatientId()).withPatient(patient)).toList();

        when(visitService.findAllVisitByDoctor(doctorId)).thenReturn(visits);
        when(doctorDAO.findById(doctorId)).thenReturn(doctor);

        // when, then
        mockMvc.perform(get(VISITS.concat(DOCTOR_ID), doctorId))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/doctor-visits"))
                .andExpect(model().attribute("visits", visits))
                .andExpect(model().attribute("doctor", doctor));

        verify(visitService, times(1)).findAllVisitByDoctor(doctorId);
        verify(doctorDAO, times(1)).findById(doctorId);
    }

    @Test
    public void testShowVisitsByPatient() throws Exception {
        // given
        Integer doctorId = 1, patientId = 2;
        DoctorDTO doctor = DtoFixtures.someDoctor1();
        PatientDTO patient = DtoFixtures.somePatient2();
        List<VisitDTO> visits = DtoFixtures.visits.stream().map(visit -> visit.withDoctorId(doctorId).withDoctor(doctor)
                .withPatientId(patient.getPatientId()).withPatient(patient)).toList();

        when(visitService.findAllVisitByPatient(patientId)).thenReturn(visits);
        when(patientDAO.findById(patientId)).thenReturn(patient);

        // when, then
        mockMvc.perform(get(VISITS.concat(PATIENT_ID), patientId))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patient-history"))
                .andExpect(model().attribute("visits", visits))
                .andExpect(model().attribute("patient", patient));

        verify(visitService, times(1)).findAllVisitByPatient(patientId);
        verify(patientDAO, times(1)).findById(patientId);
    }

    @Test
    public void testAddOpinionsForVisits() throws Exception {
        // given
        Integer patientId = 1;
        PatientDTO patient = DtoFixtures.somePatient1();
        DoctorDTO doctor = DtoFixtures.someDoctor1();
        List<VisitDTO> visits = DtoFixtures.visits.stream().map(visit -> visit.withDoctorId(doctor.getDoctorId()).withDoctor(doctor)
                .withPatientId(patientId).withPatient(patient)).toList();

        when(visitService.findAllVisitByPatient(patientId)).thenReturn(visits);
        when(patientDAO.findById(patientId)).thenReturn(patient);
        when(authentication.getName()).thenReturn("Stefan");

        // when, then
        mockMvc.perform(get(VISITS.concat(PATIENT_ADD_OPINION), patientId))
                .andExpect(status().isOk())
                .andExpect(view().name("opinion/opinion-to-add"))
                .andExpect(model().attribute("visits", visits))
                .andExpect(model().attribute("patient", patient));

        verify(visitService, times(1)).findAllVisitByPatient(patientId);
        verify(patientDAO, times(1)).findById(patientId);
    }

    @Test
    public void testAddVisit() throws Exception {
        // given
        Integer doctorId = 1, patientId = 2;
        LocalDateTime dateTime = LocalDateTime.now();
        String note = "Notatka z wizyty";
        String status = "odbyta";

        // when, then
        mockMvc.perform(post(VISITS.concat(ADD))
                        .param("doctorId", doctorId.toString())
                        .param("patientId", patientId.toString())
                        .param("dateTime", dateTime.toString())
                        .param("note", note)
                        .param("status", status)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(visitDAO, times(1)).save(any(VisitEntity.class));
    }

    @Test
    public void testGenerateVisit() throws Exception {
        // given
        Integer doctorId = 1, patientId = 2;
        String date = "2023-08-10";
        String time = "14:30";
        String note = "Wizyta pacjenta itd.";

        // when, then
        mockMvc.perform(post(VISITS.concat(GENERATE))
                        .param("doctorId", doctorId.toString())
                        .param("patientId", patientId.toString())
                        .param("date", date)
                        .param("time", time)
                        .param("note", note)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(visitDAO, times(1)).save(any(VisitEntity.class));
    }

    @Test
    public void testUpdateVisitById() throws Exception {
        // given
        Integer visitId = 1, doctorId = 2, patientId = 3;
        LocalDateTime dateTime = LocalDateTime.now();
        String note = "Aktualizowana notatka";
        String status = "odbyta";

        DoctorEntity doctorEntity = EntityFixtures.someDoctor2();
        PatientEntity patientEntity = EntityFixtures.somePatient3();
        VisitEntity visitEntity = EntityFixtures.someVisit1().withDoctor(doctorEntity).withPatient(patientEntity);

        when(doctorDAO.findEntityById(doctorId)).thenReturn(doctorEntity);
        when(patientDAO.findEntityById(patientId)).thenReturn(patientEntity);
        when(visitDAO.findEntityById(visitId)).thenReturn(visitEntity);

        // when, then
        mockMvc.perform(put(VISITS.concat(UPDATE_ID), visitId)
                        .param("doctorId", doctorId.toString())
                        .param("patientId", patientId.toString())
                        .param("dateTime", dateTime.toString())
                        .param("note", note)
                        .param("status", status)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(patientDAO, times(1)).findEntityById(patientId);
        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).save(visitEntity);

        verifyNoMoreInteractions(doctorDAO, patientDAO, visitDAO);
    }

    @Test
    public void testUpdateVisit() throws Exception {
        // given
//        Integer visitId = 1;
        DoctorEntity doctor = EntityFixtures.someDoctor2();
        PatientEntity patient = EntityFixtures.somePatient4();
        VisitEntity visitEntity = EntityFixtures.someVisit1().withDoctorId(doctor.getDoctorId()).withDoctor(doctor).withPatientId(patient.getPatientId()).withPatient(patient);
        VisitEntity updateVisit = visitEntity.withNote("Jakaś notatka");

        when(doctorDAO.findEntityById(anyInt())).thenReturn(doctor);
        when(patientDAO.findEntityById(anyInt())).thenReturn(patient);
        when(visitDAO.findEntityById(anyInt())).thenReturn(visitEntity);

        // when, then
        mockMvc.perform(put(VISITS.concat(UPDATE))
                        .flashAttr("updateVisit", updateVisit)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(doctorDAO, times(1)).findEntityById(anyInt());
        verify(patientDAO, times(1)).findEntityById(anyInt());
        verify(visitDAO, times(1)).findEntityById(anyInt());
        verify(visitDAO, times(1)).save(visitEntity);

        verifyNoMoreInteractions(doctorDAO, patientDAO, visitDAO);
    }

    @Test
    public void testUpdateVisitNote() throws Exception {
        // given
        Integer visitId = 5;
        String newNote = "Nowa notatka do wizyty";
        VisitEntity visitEntity = EntityFixtures.someVisit5();

        when(visitDAO.findEntityById(visitId)).thenReturn(visitEntity);

        // when, then
        mockMvc.perform(patch(VISITS.concat(UPDATE_NOTE), visitId)
                        .content(newNote)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).save(visitEntity);

        verifyNoMoreInteractions(visitDAO);
    }

    @Test
    public void testSimpleDeleteVisitById() throws Exception {
        // given
        Integer visitId = 3;
        VisitEntity visitEntity = EntityFixtures.someVisit3();

        when(visitDAO.findEntityById(visitId)).thenReturn(visitEntity);

        // when, then
        mockMvc.perform(delete(VISITS.concat(DELETE_BY_ID_SIMPLE), visitId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).delete(visitEntity);

        verifyNoMoreInteractions(visitDAO);
    }

    @Test
    public void testDeleteVisitById() throws Exception {
        // given
        Integer visitId = 4;
        VisitEntity visitEntity = EntityFixtures.someVisit4();
        String referer = "http://example.com";

        when(visitDAO.findEntityById(visitId)).thenReturn(visitEntity);

        // when, then
        mockMvc.perform(delete(VISITS.concat(DELETE_BY_ID), visitId)
                        .with(csrf())
                        .header("Referer", referer))
                .andExpect(status().isSeeOther())
                .andExpect(redirectedUrl(referer));

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).delete(visitEntity);

        verifyNoMoreInteractions(visitDAO);
    }

    @Test
    public void testDeleteVisitByIdNotFound() throws Exception {
        // given
        int visitId = 1;

        when(visitDAO.findEntityById(visitId)).thenReturn(null);

        // when, then
        mockMvc.perform(delete(VISITS.concat(DELETE_BY_ID), visitId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(visitDAO, never()).delete(any());
    }
}