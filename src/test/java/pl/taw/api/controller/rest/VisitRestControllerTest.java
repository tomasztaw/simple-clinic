package pl.taw.api.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.VisitEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class VisitRestControllerTest {

    @Mock // atrapa
    private VisitDAO visitDAO;

    @InjectMocks // wstrzykiwanie
    private VisitRestController visitRestController;

    @BeforeEach // inicjalizacja atrapy przed każdym testem
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnsListOfVisits() {
        // given
        List<VisitDTO> visits = Arrays.asList(new VisitDTO(), new VisitDTO());
        when(visitDAO.findAll()).thenReturn(visits);

        // when
        ResponseEntity<List<VisitDTO>> response = visitRestController.getAllVisits();

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(visits, response.getBody());
        verify(visitDAO, times(1)).findAll();
    }

    @Test
    void shouldReturnExistingVisit() {
        // given
        Integer visitId = 1;
        VisitDTO visit = new VisitDTO();
        when(visitDAO.findById(visitId)).thenReturn(visit);

        // when
        ResponseEntity<VisitDTO> response = visitRestController.getVisitById(visitId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(visit, response.getBody());
        verify(visitDAO, times(1)).findById(visitId);
    }

    @Test
    void shouldReturnNotFoundWhenVisitNonExist() {
        // given
        Integer visitId = 1;
        when(visitDAO.findById(visitId)).thenReturn(null);

        // when
        ResponseEntity<VisitDTO> response = visitRestController.getVisitById(visitId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(visitDAO, times(1)).findById(visitId);
    }

    @Test
    void shouldCreateVisitAndReturnStatusCreated() {
        // given
        VisitEntity visit = new VisitEntity();
        VisitEntity createdVisit = new VisitEntity();
        when(visitDAO.saveAndReturn(visit)).thenReturn(createdVisit);

        // when
        ResponseEntity<VisitEntity> response = visitRestController.createVisit(visit);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdVisit, response.getBody());
        verify(visitDAO, times(1)).saveAndReturn(visit);
    }

    @Test
    void shouldUpdateVisitAndReturnUpdateVisit() {
        // given
        Integer visitId = 1;
        VisitEntity existingVisit = new VisitEntity();
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
        Integer visitId = 1;
        VisitEntity existingVisit = new VisitEntity();
        when(visitDAO.findEntityById(visitId)).thenReturn(existingVisit);

        // when
        ResponseEntity<Void> response = visitRestController.deleteVisit(visitId);

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
        ResponseEntity<Void> response = visitRestController.deleteVisit(visitId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(visitDAO, times(1)).findEntityById(visitId);
        verify(visitDAO, never()).delete(any());
    }

}
