package pl.taw.api.controller.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorRestControllerMockitoTest {

    @Mock
    private DoctorDAO doctorDAO;

    @Mock
    private VisitService visitService;

    @InjectMocks
    private DoctorRestController doctorRestController;


    @Test
    void shouldReturnsAllDoctors() {
        // given
        DoctorsDTO doctors = DoctorsDTO.of(DtoFixtures.doctors);

        when(doctorDAO.findAll()).thenReturn(doctors.getDoctors());

        // when
        DoctorsDTO result = doctorRestController.getDoctors();

        // then
        assertNotNull(result);
        assertEquals(doctors.getDoctors().size(), result.getDoctors().size());

        verify(doctorDAO, times(1)).findAll();
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void shouldReturnDoctorDetails() {
        // given
        Integer doctorId = 3;
        DoctorDTO existingDoctor = DtoFixtures.someDoctor3();

        when(doctorDAO.findById(doctorId)).thenReturn(existingDoctor);

        // when
        DoctorDTO result = doctorRestController.showDoctorDetails(doctorId);

        // then
        assertNotNull(result);
        assertEquals(existingDoctor, result);

        verify(doctorDAO, times(1)).findById(doctorId);
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void shouldReturnAllHistoryForDoctor() {
        // given
        Integer doctorId = 2;
        DoctorDTO doctor = DtoFixtures.someDoctor2();
        List<VisitDTO> visitList = DtoFixtures.visits.stream().map(visit -> visit.withDoctorId(doctorId)).toList();
        VisitsDTO visits = VisitsDTO.of(visitList);

        when(doctorDAO.findById(doctorId)).thenReturn(doctor);
        when(visitService.findAllVisitByDoctor(doctorId)).thenReturn(visits.getVisits());

        // when
        ResponseEntity<VisitsDTO> response = doctorRestController.showHistory(doctorId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(visits, response.getBody());
        assertTrue(Objects.requireNonNull(response.getBody()).getVisits().stream().allMatch(visit -> visit.getDoctorId().equals(doctorId)));

        verify(visitService, times(1)).findAllVisitByDoctor(doctorId);
        verify(doctorDAO, times(1)).findById(doctorId);
        verifyNoMoreInteractions(visitService, doctorDAO);
    }

    @Test
    void addDoctorShouldWorksCorrectly() {
        // given
        DoctorDTO doctorDTO = DtoFixtures.someDoctor3();
        DoctorEntity doctorEntity = EntityFixtures.someDoctor3();

        when(doctorDAO.saveAndReturn(any(DoctorEntity.class))).thenReturn(doctorEntity);

        // when
        ResponseEntity<DoctorDTO> response = doctorRestController.addDoctor(doctorDTO);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/doctors/%s".formatted(doctorEntity.getDoctorId()),
                Objects.requireNonNull(response.getHeaders().getLocation()).toString());

        verify(doctorDAO, times(1)).saveAndReturn(any(DoctorEntity.class));
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void updateDoctorShouldWorksCorrectly() {
        // given
        Integer doctorId = 3;
        DoctorDTO doctorDTO = DtoFixtures.someDoctor3();
        DoctorEntity doctorEntity = EntityFixtures.someDoctor3();

        when(doctorDAO.findEntityById(doctorId)).thenReturn(doctorEntity);

        // when
        ResponseEntity<?> response = doctorRestController.updateDoctor(doctorId, doctorDTO);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(doctorDAO, times(1)).save(doctorEntity);
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void deleteDoctorShouldWorksCorrectly() {
        // given
        Integer doctorId = 1;
        DoctorEntity existingDoctor = EntityFixtures.someDoctor1();

        when(doctorDAO.findEntityById(doctorId)).thenReturn(existingDoctor);

        // when
        ResponseEntity<?> response = doctorRestController.deleteDoctor(doctorId);

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(doctorDAO, times(1)).delete(existingDoctor);
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void updateDoctorTitleShouldWorksCorrectly() {
        // given
        Integer doctorId = 2;
        String newTitle = "Najlepszy z najlepszych";
        DoctorEntity existingDoctor = EntityFixtures.someDoctor2();

        when(doctorDAO.findEntityById(doctorId)).thenReturn(existingDoctor);

        // when
        ResponseEntity<?> response = doctorRestController.updateDoctorTitle(doctorId, newTitle);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(doctorDAO, times(1)).save(existingDoctor);
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void updateDoctorPhoneShouldWorksCorrectly() {
        // given
        Integer doctorId = 1;
        String newPhone = "+48 120 235 588";
        DoctorEntity existingDoctor = EntityFixtures.someDoctor1();
        when(doctorDAO.findEntityById(doctorId)).thenReturn(existingDoctor);

        // when
        ResponseEntity<?> response = doctorRestController.updateDoctorPhone(doctorId, newPhone);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(doctorDAO, times(1)).save(existingDoctor);
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void updateDoctorEmailShouldWorksCorrectly() {
        // given
        Integer doctorId = 3;
        String newEmail = "nowy.mail@eclinic.pl";
        DoctorEntity existingDoctor = EntityFixtures.someDoctor3();

        when(doctorDAO.findEntityById(doctorId)).thenReturn(existingDoctor);

        // when
        ResponseEntity<?> response = doctorRestController.updateDoctorEmail(doctorId, newEmail);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(doctorDAO, times(1)).findEntityById(doctorId);
        verify(doctorDAO, times(1)).save(existingDoctor);
        verifyNoMoreInteractions(doctorDAO);
    }

    @Test
    void updateDoctorWithInvalidEmailShouldReturnBadRequest() {
        // given
        Integer doctorId = 5;
        String newEmail = "zly.email#eclinic.pl";

        // when
        ResponseEntity<?> response = doctorRestController.updateDoctorEmail(doctorId, newEmail);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(doctorDAO, never()).findEntityById(doctorId);
        verify(doctorDAO, never()).save(any());
    }


}