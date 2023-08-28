package pl.taw.api.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.api.dto.ReservationsDTO;
import pl.taw.business.ReservationService;
import pl.taw.business.dao.ReservationDAO;
import pl.taw.infrastructure.database.entity.ReservationEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.taw.api.controller.rest.ReservationRestController.*;

@WebMvcTest(controllers = ReservationRestController.class)
@AutoConfigureMockMvc(addFilters = false) // wyłączenie filtrów security dla testów Mock Mvc
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReservationRestControllerWebMvcTest {

    @MockBean
    private ReservationDAO reservationDAO;

    @MockBean
    private ReservationService reservationService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @Test
    void getReservationsAsListShouldWorksCorrectly() throws Exception {
        // given
        ReservationsDTO reservations = DtoFixtures.reservationsDTO;

        when(reservationDAO.findAll()).thenReturn(reservations.getReservations());

        // when, then
        mockMvc.perform(get(API_RESERVATIONS))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reservations", hasSize(reservations.getReservations().size())))
                .andExpect(jsonPath("$.reservations[*]", notNullValue()));

        verify(reservationDAO, times(1)).findAll();
        verify(reservationDAO, only()).findAll();
    }

    @Test
    void reservationDetailsShouldWorksCorrectly() throws Exception {
        // given
        Integer reservationId = 1;
        ReservationDTO reservation = DtoFixtures.someReservation1();

        when(reservationDAO.findById(reservationId)).thenReturn(reservation);

        // when, then
        mockMvc.perform(get(API_RESERVATIONS.concat(RESERVATION_ID), reservationId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId", is(reservationId)))
                .andExpect(jsonPath("$.doctorId", is(reservation.getDoctorId())))
                .andExpect(jsonPath("$.patientId", is(reservation.getPatientId())));

        verify(reservationDAO, times(1)).findById(reservationId);
        verify(reservationDAO, only()).findById(reservationId);
    }

    @Test
    void testReservationDetails_NonExistingReservation() throws Exception {
        // given
        Integer reservationId = -1;

        when(reservationDAO.findById(reservationId)).thenReturn(null);

        // when, then
        mockMvc.perform(get(API_RESERVATIONS.concat(RESERVATION_ID), reservationId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());

        verify(reservationDAO, times(1)).findById(reservationId);
        verify(reservationDAO, only()).findById(reservationId);
    }

    @Test
    public void testGetAllReservationsByDateWorksCorrectly() throws Exception {
        // given
        LocalDate testDate = LocalDate.of(2023, 8, 4);
        String formattedTestDate = testDate.format(DateTimeFormatter.ISO_DATE);
        ReservationsDTO reservations = ReservationsDTO.of(DtoFixtures.reservations.stream().map(res -> res.withDay(testDate)).toList());

        Mockito.when(reservationService.findAllByDay(eq(testDate))).thenReturn(reservations.getReservations());

        // when, then
        mockMvc.perform(get(API_RESERVATIONS.concat(BY_DATE), testDate))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.reservations", hasSize(reservations.getReservations().size())))
                .andExpect(jsonPath("$.reservations[*].day", everyItem(is(formattedTestDate))));

        verify(reservationService, times(1)).findAllByDay(testDate);
        verify(reservationService, only()).findAllByDay(testDate);
    }

    @Test
    public void testAddReservationShouldWorksCorrectly() throws Exception {
        // given
        ReservationDTO inputDTO = DtoFixtures.someReservation1();
        ReservationEntity savedEntity = EntityFixtures.someReservation1();

        when(reservationDAO.saveAndReturn(ArgumentMatchers.any(ReservationEntity.class))).thenReturn(savedEntity);

        // when, then
        mockMvc.perform(post(API_RESERVATIONS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/reservations/%s"
                        .formatted(savedEntity.getReservationId())));

        verify(reservationDAO, times(1)).saveAndReturn(ArgumentMatchers.any(ReservationEntity.class));
        verify(reservationDAO, only()).saveAndReturn(ArgumentMatchers.any(ReservationEntity.class));
    }

    @Test
    public void testUpdateReservationShouldWorksCorrectly() throws Exception {
        // given
        Integer reservationId = 2;
        ReservationDTO inputDTO = DtoFixtures.someReservation2();
        ReservationEntity existingReservation = EntityFixtures.someReservation2();

        when(reservationDAO.findEntityById(eq(reservationId))).thenReturn(existingReservation);

        // when, then
        mockMvc.perform(put(API_RESERVATIONS.concat(RESERVATION_ID), reservationId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk());

        verify(reservationDAO, times(1)).save(existingReservation);
        verify(reservationDAO, times(1)).findEntityById(eq(reservationId));
        verifyNoMoreInteractions(reservationDAO);
    }

    @Test
    public void testDeleteReservationWorksCorrectly() throws Exception {
        // given
        Integer reservationId = 3;
        ReservationEntity existingReservation = EntityFixtures.someReservation3();

        when(reservationDAO.findEntityById(eq(reservationId))).thenReturn(existingReservation);

        // when, then
        mockMvc.perform(delete(API_RESERVATIONS.concat(RESERVATION_ID), reservationId))
                .andExpect(status().isNoContent());

        verify(reservationDAO, times(1)).delete(existingReservation);
        verify(reservationDAO, times(1)).findEntityById(reservationId);
        verifyNoMoreInteractions(reservationDAO);
    }

    @Test
    void deleteReservationShouldReturnNotFound() throws Exception {
        // given
        Integer reservationId = -2;

        when(reservationDAO.findEntityById(reservationId)).thenReturn(null);

        // when, then
        mockMvc.perform(delete(API_RESERVATIONS.concat(RESERVATION_ID), reservationId))
                .andExpect(status().isNotFound());

        verify(reservationDAO, never()).delete(ArgumentMatchers.any(ReservationEntity.class));
        verify(reservationDAO, only()).findEntityById(reservationId);
    }

    @Test
    public void testUpdateReservationDateShouldWorksCorrectly() throws Exception {
        // given
        Integer reservationId = 1;
        LocalDateTime dateTime = LocalDateTime.of(2023, 8, 7, 12, 30);
        ReservationEntity existingReservation = EntityFixtures.someReservation1();

        when(reservationDAO.findEntityById(eq(reservationId))).thenReturn(existingReservation);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = dateTime.format(formatter);

        // when, then
        mockMvc.perform(patch(API_RESERVATIONS.concat(RESERVATION_UPDATE_DATE), reservationId)
//                        .param("dateTime", dateTime.toString())
                        .param("dateTime", formattedDateTime)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(reservationDAO, times(1)).save(existingReservation);
        verify(reservationDAO, times(1)).findEntityById(reservationId);
    }

}