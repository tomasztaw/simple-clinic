package pl.taw.api.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.taw.api.dto.VisitDTO;
import pl.taw.api.dto.VisitsDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.taw.api.controller.rest.VisitRestController.*;

@WebMvcTest(controllers = VisitRestController.class)
@AutoConfigureMockMvc(addFilters = false) // wyłączenie filtrów security dla testów Mock Mvc
@AllArgsConstructor(onConstructor = @__(@Autowired))
class VisitRestControllerWebMvcTest {

    @MockBean
    private VisitDAO visitDAO;
    @MockBean
    @SuppressWarnings("unused")
    private DoctorDAO doctorDAO;
    @MockBean
    @SuppressWarnings("unused")
    private PatientDAO patientDAO;
    @MockBean
    private VisitService visitService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;


    @Test
    void shouldReturnsListOfVisits() throws Exception {
        // given
        VisitsDTO visits = VisitsDTO.of(DtoFixtures.visits);

        when(visitDAO.findAll()).thenReturn(visits.getVisits());

        // when, then
        mockMvc.perform(get(API_VISITS))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.visits", hasSize(visits.getVisits().size())))
                .andExpect(jsonPath("$.visits[*]", notNullValue()));

        verify(visitDAO, times(1)).findAll();
        verify(visitDAO, only()).findAll();
    }

    @Test
    void shouldReturnExistingVisit() throws Exception {
        // given
        Integer visitId = 1;
        VisitDTO visit = DtoFixtures.someVisit1();

        when(visitDAO.findById(visitId)).thenReturn(visit);

        // when, then
        mockMvc.perform(get(API_VISITS.concat(VISIT_ID), visitId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visitId", is(visitId)))
                .andExpect(content().json(objectMapper.writeValueAsString(visit)));

        verify(visitDAO, times(1)).findById(visitId);
        verify(visitDAO, only()).findById(visitId);
    }

    @Test
    void shouldReturnNotFoundWhenVisitNonExist() throws Exception {
        // given
        Integer visitId = -12;

        when(visitDAO.findById(visitId)).thenReturn(null);

        // when, then
        mockMvc.perform(get(API_VISITS.concat(VISIT_ID), visitId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(visitDAO, times(1)).findById(visitId);
        verify(visitDAO, only()).findById(visitId);
    }

    @Test
    void shouldCreateVisitAndReturnStatusCreated() throws Exception {
        // given
        VisitDTO visitDTO = DtoFixtures.someVisit4();
        VisitEntity visitEntity = EntityFixtures.someVisit1();

//        when(visitDAO.saveAndReturn(ArgumentMatchers.any(VisitEntity.class))).thenReturn(visitEntity);
        when(visitService.saveVisit(ArgumentMatchers.any(VisitEntity.class))).thenReturn(visitEntity);

        // when, then
        mockMvc.perform(post(API_VISITS)
                        .content(objectMapper.writeValueAsString(visitDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/visits/%s"
                        .formatted(visitEntity.getVisitId())));

//        verify(visitDAO, times(1)).saveAndReturn(ArgumentMatchers.any(VisitEntity.class));
//        verify(visitDAO, only()).saveAndReturn(ArgumentMatchers.any(VisitEntity.class));
        verify(visitService, times(1)).saveVisit(ArgumentMatchers.any(VisitEntity.class));
        verify(visitService, only()).saveVisit(ArgumentMatchers.any(VisitEntity.class));
    }

    @Test
    void shouldUpdateVisitAndReturnUpdateVisit() throws Exception {
        // given
        Integer visitId = 1;
        VisitEntity existingVisit = EntityFixtures.someVisit2();
        VisitDTO updatedVisit = DtoFixtures.someVisit1();

        when(visitDAO.findEntityById(visitId)).thenReturn(existingVisit);

        // when, then
        mockMvc.perform(put(API_VISITS.concat(VISIT_ID), visitId)
                        .content(objectMapper.writeValueAsString(updatedVisit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).save(existingVisit);
        verifyNoMoreInteractions(visitDAO);
    }

    @Test
    void shouldReturnNotFoundWhenTryToUpdateNonExistingVisit() throws Exception {
        // given
        Integer visitId = 1;
        VisitEntity existingVisit = EntityFixtures.someVisit1();
        VisitDTO visitDTO = DtoFixtures.someVisit1();

        when(visitDAO.findEntityById(visitId)).thenReturn(null);

        // when, then
        mockMvc.perform(put(API_VISITS.concat(VISIT_ID), visitId)
                        .content(objectMapper.writeValueAsString(visitDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, never()).save(existingVisit);
        verify(visitDAO, only()).findEntityById(visitId);
    }

    @Test
    void shouldDeleteExistingVisitAndReturnNoContent() throws Exception {
        // given
        Integer visitId = 5;
        VisitEntity existingVisit = EntityFixtures.someVisit5();

        when(visitDAO.findEntityById(visitId)).thenReturn(existingVisit);

        // when, then
        mockMvc.perform(delete(API_VISITS.concat(VISIT_ID), visitId))
                .andExpect(status().isNoContent());

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).delete(existingVisit);
        verifyNoMoreInteractions(visitDAO);
    }

    @Test
    void shouldReturnNotFoundWhenTryToDeleteNonExistingVisit() throws Exception {
        // given
        Integer visitId = -1;
        when(visitDAO.findEntityById(visitId)).thenReturn(null);

        // when, then
        mockMvc.perform(delete(API_VISITS.concat(VISIT_ID), visitId))
                .andExpect(status().isNotFound());

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, never()).delete(any());
        verify(visitDAO, only()).findEntityById(visitId);
    }

    @Test
    void shouldUpdateVisitNoteAndReturnOk() throws Exception {
        // given
        Integer visitId = 4;
        String updatedNote = "Poprawiona notatka do wizyty";
        VisitEntity existingVisit = EntityFixtures.someVisit4();

        when(visitDAO.findEntityById(visitId)).thenReturn(existingVisit);

        // when, then
        mockMvc.perform(patch(API_VISITS.concat(VISIT_UPDATE_NOTE), visitId)
                        .param("updatedNote", updatedNote)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).save(existingVisit);
        verifyNoMoreInteractions(visitDAO);
    }

}
