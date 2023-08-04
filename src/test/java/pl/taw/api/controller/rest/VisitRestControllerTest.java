package pl.taw.api.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.taw.api.dto.VisitDTO;
import pl.taw.api.dto.VisitsDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VisitRestControllerTest {

    @Mock // atrapa
    private VisitDAO visitDAO;

    @Mock
    private DoctorDAO doctorDAO;

    @Mock
    private PatientDAO patientDAO;

    @InjectMocks // wstrzykiwanie
    private VisitRestController visitRestController;

    @BeforeEach // inicjalizacja atrapy przed każdym testem
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnsListOfVisits() {
        // given
        VisitsDTO visits = VisitsDTO.of(DtoFixtures.visits);
        when(visitDAO.findAll()).thenReturn(visits.getVisits());

        // when
        VisitsDTO response = visitRestController.getAllVisits();

        // then
        assertNotNull(response);
        assertEquals(visits.getVisits().size(), response.getVisits().size());
        verify(visitDAO, times(1)).findAll();
    }


    @Test
    void shouldReturnExistingVisit() {
        // given
        Integer visitId = 1;
        VisitDTO visit = DtoFixtures.someVisit1();
        when(visitDAO.findById(visitId)).thenReturn(visit);

        // when
        VisitDTO response = visitRestController.visitDetails(visitId);

        // then
        assertNotNull(response);
        assertEquals(visit, response);
        verify(visitDAO, times(1)).findById(visitId);
    }

    @Test
    void shouldReturnNotFoundWhenVisitNonExist() {
        // given
        Integer visitId = 1;
        when(visitDAO.findById(visitId)).thenReturn(null);

        // when
        VisitDTO response = visitRestController.visitDetails(visitId);

        // then
        assertNull(response);
        verify(visitDAO, times(1)).findById(visitId);
    }

    // TODO mam problem z metodą saveAndReturn
//    @Test
//    void shouldCreateVisitAndReturnStatusCreated() {
//        // given
//        VisitDTO visitDTO = DtoFixtures.someVisit1();
//        VisitEntity visitEntity = EntityFixtures.someVisit1();
//        VisitEntity createdVisit = EntityFixtures.someVisit1();
//        when(visitDAO.saveAndReturn(visitEntity)).thenReturn(createdVisit);
//
//        // when
//        ResponseEntity<VisitDTO> response = visitRestController.addVisit(visitDTO);
//
//        // then
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(visitDTO, response.getBody());
//        verify(visitDAO, times(1)).saveAndReturn(visitEntity);
//    }

    @Test
    void shouldUpdateVisitAndReturnUpdateVisit() {
        // given
        Integer visitId = 1;
//        VisitEntity existingVisit = EntityFixtures.someVisit2();
        VisitEntity existingVisit = new VisitEntity();
//        VisitEntity updatedVisit = EntityFixtures.someVisit3();
        VisitEntity updatedVisit = new VisitEntity();
        when(visitDAO.findEntityById(visitId)).thenReturn(existingVisit);
        when(visitDAO.saveAndReturn(updatedVisit)).thenReturn(updatedVisit);

        // when
        ResponseEntity<VisitEntity> response = visitRestController.updateVisit(visitId, updatedVisit);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedVisit, response.getBody());
        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).saveAndReturn(updatedVisit);
    }

    @Test
    void shouldReturnNotFoundWhenTryToUpdateNonExistingVisit() {
        // given
        Integer visitId = 1;
        VisitEntity updatedVisit = new VisitEntity();
        when(visitDAO.findEntityById(visitId)).thenReturn(null);

        // when
        ResponseEntity<VisitEntity> response = visitRestController.updateVisit(visitId, updatedVisit);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, never()).saveAndReturn(updatedVisit);
    }

    @Test
    void shouldDeleteExistingVisitAndReturnNoContent() {
        // given
        Integer visitId = 5;
        VisitEntity existingVisit = EntityFixtures.someVisit5();
        when(visitDAO.findEntityById(visitId)).thenReturn(existingVisit);

        // when
        ResponseEntity<?> response = visitRestController.deleteVisit(visitId);

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).delete(existingVisit);
    }

    @Test
    void shouldReturnNotFoundWhenTryToDeleteNonExistingVisit() {
        // given
        Integer visitId = 1;
        when(visitDAO.findEntityById(visitId)).thenReturn(null);

        // when
        ResponseEntity<?> response = visitRestController.deleteVisit(visitId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, never()).delete(any());
    }

    @Test
    void shouldUpdateVisitNoteAndReturnOk() {
        // given
        Integer visitId = 4;
        String updatedNote = "Poprawiona notatka do wizyty";
        VisitEntity existingVisit = EntityFixtures.someVisit4();

        when(visitDAO.findEntityById(visitId)).thenReturn(existingVisit);

        // when
        ResponseEntity<?> response = visitRestController.updateVisitNote(visitId, updatedNote);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedNote, existingVisit.getNote());
        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).save(existingVisit);
    }


}
