package pl.taw.api.controller.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitRestControllerMockitoTest {

    @Mock // atrapa
    private VisitDAO visitDAO;

    // Pomimo tego, że te zaślepki są nieużywane, to bez nich nie przechodzi test "should Update Visit And...."
    @Mock
    @SuppressWarnings("unused")
    private DoctorDAO doctorDAO;
    @Mock
    @SuppressWarnings("unused")
    private PatientDAO patientDAO;

    @InjectMocks // wstrzykiwanie
    private VisitRestController visitRestController;


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
        verify(visitDAO, only()).findAll();
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
        verify(visitDAO, only()).findById(visitId);
    }

    @Test
    void shouldReturnNotFoundWhenVisitNonExist() {
        // given
        Integer visitId = -12;

        when(visitDAO.findById(visitId)).thenReturn(null);

        // when
        VisitDTO response = visitRestController.visitDetails(visitId);

        // then
        assertNull(response);

        verify(visitDAO, times(1)).findById(visitId);
        verify(visitDAO, only()).findById(visitId);
    }

    @Test
    void shouldCreateVisitAndReturnStatusCreated() {
        // given
        VisitDTO visitDTO = DtoFixtures.someVisit4();
        VisitEntity visitEntity = EntityFixtures.someVisit1();

        when(visitDAO.saveAndReturn(ArgumentMatchers.any(VisitEntity.class))).thenReturn(visitEntity);

        // when
        ResponseEntity<VisitDTO> response = visitRestController.addVisit(visitDTO);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("/api/visits/%s".formatted(visitEntity.getVisitId())), response.getHeaders().getLocation());

        verify(visitDAO, times(1)).saveAndReturn(ArgumentMatchers.any(VisitEntity.class));
        verify(visitDAO, only()).saveAndReturn(ArgumentMatchers.any(VisitEntity.class));
    }

    @Test
    void shouldUpdateVisitAndReturnUpdateVisit() {
        // given
        Integer visitId = 1;
        VisitEntity existingVisit = EntityFixtures.someVisit2();
        VisitDTO updatedVisit = DtoFixtures.someVisit1();

        when(visitDAO.findEntityById(visitId)).thenReturn(existingVisit);

        // when
        ResponseEntity<VisitEntity> response = visitRestController.updateVisit(visitId, updatedVisit);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingVisit, response.getBody());

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, times(1)).save(existingVisit);
        verifyNoMoreInteractions(visitDAO);
    }

    @Test
    void shouldReturnNotFoundWhenTryToUpdateNonExistingVisit() {
        // given
        Integer visitId = 1;
        VisitEntity existingVisit = EntityFixtures.someVisit1();
        VisitDTO visitDTO = DtoFixtures.someVisit1();

        when(visitDAO.findEntityById(visitId)).thenReturn(null);

        // when
        ResponseEntity<VisitEntity> response = visitRestController.updateVisit(visitId, visitDTO);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, never()).save(existingVisit);
        verify(visitDAO, only()).findEntityById(visitId);
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
        verifyNoMoreInteractions(visitDAO);
    }

    @Test
    void shouldReturnNotFoundWhenTryToDeleteNonExistingVisit() {
        // given
        Integer visitId = -1;
        when(visitDAO.findEntityById(visitId)).thenReturn(null);

        // when
        ResponseEntity<?> response = visitRestController.deleteVisit(visitId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, never()).delete(any());
        verify(visitDAO, only()).findEntityById(visitId);
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
        verifyNoMoreInteractions(visitDAO);
    }

}
