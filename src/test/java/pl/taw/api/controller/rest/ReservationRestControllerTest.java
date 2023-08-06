package pl.taw.api.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.taw.api.dto.PatientsDTO;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.api.dto.ReservationsDTO;
import pl.taw.business.ReservationService;
import pl.taw.business.VisitService;
import pl.taw.business.dao.PatientDAO;
import pl.taw.business.dao.ReservationDAO;
import pl.taw.infrastructure.database.entity.ReservationEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationRestControllerTest {


    @Mock
    private ReservationDAO reservationDAO;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationRestController reservationRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getReservationsAsListShouldWorksCorrectly() {
        // given
        ReservationsDTO reservations = DtoFixtures.reservationsDTO;
        when(reservationDAO.findAll()).thenReturn(reservations.getReservations());

        // when
        ReservationsDTO result = reservationRestController.getReservationsAsList();

        // then
        assertThat(result, is(notNullValue()));
        assertThat(result.getReservations(), hasSize(reservations.getReservations().size()));
        assertThat(result.getReservations(), containsInAnyOrder(reservations.getReservations().toArray()));
        verify(reservationDAO, times(1)).findAll();
    }

    @Test
    void reservationDetailsShouldWorksCorrectly() {
        // given
        Integer reservationId = 1;
        ReservationDTO reservation = DtoFixtures.someReservation1();
        when(reservationDAO.findById(reservationId)).thenReturn(reservation);

        // when
        ReservationDTO result = reservationRestController.reservationDetails(reservationId);

        // then
        assertThat(result, is(notNullValue()));
        assertSame(reservation, result);
        verify(reservationDAO, times(1)).findById(reservationId);
    }

    @Test
    void testReservationDetails_NonExistingReservation() {
        // given
        Integer reservationId = -1;
        when(reservationDAO.findById(reservationId)).thenReturn(null);

        // when
        ReservationDTO result = reservationRestController.reservationDetails(reservationId);

        // then
        assertNull(result);
    }

    @Test
    public void testGetAllReservationsByDateWorksCorrectly() {
        // given
        LocalDate testDate = LocalDate.of(2023, 8, 4);
        ReservationsDTO reservationsDTO = ReservationsDTO.of(DtoFixtures.reservations.stream().map(res -> res.withDay(testDate)).toList());
        Mockito.when(reservationService.findAllByDay(eq(testDate))).thenReturn(reservationsDTO.getReservations());

        // when
        ReservationsDTO result = reservationRestController.getAllReservationsByDate(testDate);

        // then
        assertThat(result, is(notNullValue()));
        assertEquals(reservationsDTO.getReservations().size(), result.getReservations().size());
        assertTrue(result.getReservations().stream().allMatch(res -> res.getDay().equals(testDate)));
        assertFalse(result.getReservations().stream().anyMatch(res -> res.getDay().getDayOfWeek().equals(DayOfWeek.SUNDAY)));
        assertFalse(result.getReservations().stream().anyMatch(res -> res.getDay().getDayOfWeek().equals(DayOfWeek.SATURDAY)));
        verify(reservationService, times(1)).findAllByDay(testDate);
    }

    @Test
    public void testAddReservationShouldWorksCorrectly() {
        // given
        ReservationDTO inputDTO = DtoFixtures.someReservation1();
        ReservationEntity savedEntity = EntityFixtures.someReservation1();

        when(reservationDAO.saveAndReturn(ArgumentMatchers.any(ReservationEntity.class))).thenReturn(savedEntity);

        // when
        ResponseEntity<ReservationDTO> response = reservationRestController.addReservation(inputDTO);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("/api/reservations/%s".formatted(savedEntity.getReservationId())), response.getHeaders().getLocation());
        verify(reservationDAO, times(1)).saveAndReturn(savedEntity);
    }

    @Test
    public void testUpdateReservationShouldWorksCorrectly() {
        // given
        Integer reservationId = 2;
        ReservationDTO inputDTO = DtoFixtures.someReservation2();
        ReservationEntity existingReservation = EntityFixtures.someReservation2();

        when(reservationDAO.findEntityById(eq(reservationId))).thenReturn(existingReservation);

        // when
        ResponseEntity<?> response = reservationRestController.updateReservation(reservationId, inputDTO);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reservationDAO, times(1)).save(existingReservation);
    }

    @Test
    public void testDeleteReservationWorksCorrectly() {
        // given
        Integer reservationId = 3;
        ReservationEntity existingReservation = EntityFixtures.someReservation3();

        when(reservationDAO.findEntityById(eq(reservationId))).thenReturn(existingReservation);

        // when
        ResponseEntity<?> response = reservationRestController.deleteReservation(reservationId);

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reservationDAO, times(1)).delete(existingReservation);
    }

    @Test
    void deleteReservationShouldReturnNotFound() {
        // given
        Integer reservationId = -2;
        when(reservationDAO.findEntityById(reservationId)).thenReturn(null);

        // when
        ResponseEntity<?> response = reservationRestController.deleteReservation(reservationId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(reservationDAO, never()).delete(ArgumentMatchers.any(ReservationEntity.class));
    }

    @Test
    public void testUpdateReservationDateShouldWorksCorrectly() {
        // given
        Integer reservationId = 1;
        LocalDateTime newDateTime = LocalDateTime.of(2023, 8, 7, 12, 30);
        ReservationEntity existingReservation = EntityFixtures.someReservation1();

        when(reservationDAO.findEntityById(eq(reservationId))).thenReturn(existingReservation);

        // when
        ResponseEntity<?> response = reservationRestController.updateReservationDate(reservationId, newDateTime);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newDateTime.toLocalDate(), existingReservation.getDay());
        assertEquals(newDateTime.toLocalTime(), existingReservation.getStartTimeR());
        verify(reservationDAO).save(existingReservation);
    }

}