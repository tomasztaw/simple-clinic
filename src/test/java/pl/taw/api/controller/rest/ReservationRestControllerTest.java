package pl.taw.api.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.taw.api.dto.PatientsDTO;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.api.dto.ReservationsDTO;
import pl.taw.business.ReservationService;
import pl.taw.business.VisitService;
import pl.taw.business.dao.PatientDAO;
import pl.taw.business.dao.ReservationDAO;
import pl.taw.util.DtoFixtures;

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
    void getAllReservationsByDate() {
    }

    @Test
    void addReservation() {
    }

    @Test
    void updateReservation() {
    }

    @Test
    void deleteReservation() {
    }

    @Test
    void updateReservationDate() {
    }
}