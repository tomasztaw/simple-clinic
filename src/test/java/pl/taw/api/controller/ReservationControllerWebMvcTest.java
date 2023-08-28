package pl.taw.api.controller;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.business.dao.ReservationDAO;
import pl.taw.infrastructure.database.entity.ReservationEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.taw.api.controller.ReservationController.*;


@WebMvcTest(controllers = ReservationController.class)
@AutoConfigureMockMvc(addFilters = false) // wyłączenie filtrów security dla testów Mock Mvc
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReservationControllerWebMvcTest {

    private final MockMvc mockMvc;

    @MockBean
    private final ReservationDAO reservationDAO;
    @MockBean
    private final DoctorDAO doctorDAO;
    @MockBean
    private final PatientDAO patientDAO;
    @MockBean
    private final Authentication authentication;


    @Test
    public void testShowReservationPanel() throws Exception {
        // given
        List<ReservationDTO> reservations = DtoFixtures.reservations;
        String username = authentication.getName();

        when(reservationDAO.findAll()).thenReturn(reservations);

        // when, then
        mockMvc.perform(get(RESERVATIONS.concat(PANEL)))
                .andExpect(status().isOk())
                .andExpect(view().name("reservation/reservation-panel"))
                .andExpect(model().attribute("reservations", reservations))
                .andExpect(model().attribute("username", username))
                .andExpect(model().attributeExists("updateReservation"));

        verify(reservationDAO, times(1)).findAll();

        verifyNoMoreInteractions(reservationDAO);
    }

    @Test
    public void testShowReservation() throws Exception {
        // given
        Integer reservationId = 1;
        ReservationDTO reservation = DtoFixtures.someReservation1();
        DoctorDTO doctor = DtoFixtures.someDoctor1();
        PatientDTO patient = DtoFixtures.somePatient1();

        when(reservationDAO.findById(reservationId)).thenReturn(reservation);
        when(doctorDAO.findById(reservation.getDoctorId())).thenReturn(doctor);
        when(patientDAO.findById(reservation.getPatientId())).thenReturn(patient);

        // when, then
        mockMvc.perform(get(RESERVATIONS.concat(SHOW), reservationId))
                .andExpect(status().isOk())
                .andExpect(view().name("reservation/reservation-show"))
                .andExpect(model().attribute("reservation", reservation))
                .andExpect(model().attribute("doctor", doctor))
                .andExpect(model().attribute("patient", patient));

        verify(reservationDAO, times(1)).findById(reservationId);
        verify(doctorDAO, times(1)).findById(reservation.getDoctorId());
        verify(patientDAO, times(1)).findById(reservation.getPatientId());

        verifyNoMoreInteractions(reservationDAO, doctorDAO, patientDAO);
    }

    @Test
    public void testShowDoctorReservations() throws Exception {
        // given
        Integer doctorId = 1;
        List<ReservationDTO> reservations = DtoFixtures.reservations.stream().map(res -> res.withDoctorId(doctorId)).toList();
        DoctorDTO doctor = DtoFixtures.someDoctor1();

        when(reservationDAO.findAllByDoctor(doctorId)).thenReturn(reservations);
        when(doctorDAO.findById(doctorId)).thenReturn(doctor);

        // when, then
        mockMvc.perform(get(RESERVATIONS.concat(DOCTOR_ID), doctorId))
                .andExpect(status().isOk())
                .andExpect(view().name("reservation/reservation-doctor-all"))
                .andExpect(model().attribute("reservations", reservations))
                .andExpect(model().attribute("doctor", doctor));

        verify(reservationDAO, times(1)).findAllByDoctor(doctorId);
        verify(doctorDAO, times(1)).findById(doctorId);
    }

    @Test
    public void testShowPatientReservation() throws Exception {
        // given
        Integer patientId = 2;
        List<ReservationDTO> reservations = DtoFixtures.reservations.stream().map(res -> res.withPatientId(patientId)).toList();
        PatientDTO patient = DtoFixtures.somePatient2();

        when(reservationDAO.findAllByPatient(patientId)).thenReturn(reservations);
        when(patientDAO.findById(patientId)).thenReturn(patient);

        // when, then
        mockMvc.perform(get(RESERVATIONS.concat(PATIENT_ID), patientId))
                .andExpect(status().isOk())
                .andExpect(view().name("reservation/reservation-patient-all"))
                .andExpect(model().attribute("reservations", reservations))
                .andExpect(model().attribute("patient", patient));

        verify(reservationDAO, times(1)).findAllByPatient(patientId);
        verify(patientDAO, times(1)).findById(patientId);
    }

    @Test
    public void testConfirmReservation() throws Exception {
        // given
        Integer reservationId = 1;
        LocalDateTime fullDate = LocalDateTime.of(2023, 8, 8, 10, 10);

        // when, then
        mockMvc.perform(get(RESERVATIONS.concat(CONFIRM))
                        .param("reservationId", reservationId.toString())
                        .param("fullDate", fullDate.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("reservation/reservation-confirm"))
                .andExpect(model().attribute("reservationId", reservationId))
                .andExpect(model().attribute("fullDate", fullDate));
    }

    @Test
    public void testAddReservation() throws Exception {
        // given
        Integer doctorId = 1, patientId = 2;
        LocalDate day = LocalDate.of(2023, 8, 8);
        LocalTime startTimeR = LocalTime.of(10, 10);

        // when, then
        mockMvc.perform(post(RESERVATIONS.concat(ADD))
                        .param("doctorId", doctorId.toString())
                        .param("patientId", patientId.toString())
                        .param("day", day.toString())
                        .param("startTimeR", startTimeR.toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(reservationDAO, times(1)).save(any(ReservationEntity.class));
    }

    @Test
    public void testCreateReservation() throws Exception {
        // given
        Integer doctorId = 1, patientId = 2;
        String day = "2023-08-18";
        String startTimeR = "10:00";

        ReservationEntity savedReservation = EntityFixtures.someReservation1().withDoctorId(doctorId).withPatientId(patientId);

        when(reservationDAO.saveAndReturn(any())).thenReturn(savedReservation);

        // when, then
        mockMvc.perform(post(RESERVATIONS.concat(ADD_PARAMS), doctorId, patientId, day, startTimeR)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/reservations/confirm*"));

        verify(reservationDAO, times(1)).saveAndReturn(any());
    }

    @Test
    public void testUpdateReservation() throws Exception {
        // given
        Integer reservationId = 1;
        ReservationEntity reservationEntity = EntityFixtures.someReservation1();
        ReservationDTO updateReservationDTO = DtoFixtures.someReservation1();

        when(reservationDAO.findEntityById(reservationId)).thenReturn(reservationEntity);

        // when, then
        mockMvc.perform(put(RESERVATIONS.concat(UPDATE))
                        .flashAttr("updateReservation", updateReservationDTO)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(reservationDAO, times(1)).findEntityById(reservationId);
        verify(reservationDAO, times(1)).save(reservationEntity);
    }

    @Test
    public void testUpdateReservationTimeById() throws Exception {
        // given
        Integer reservationId = 1;
        LocalDateTime newDate = LocalDateTime.of(2023, 8, 8, 10, 50);

        ReservationEntity reservationEntity = EntityFixtures.someReservation1();

        when(reservationDAO.findEntityById(reservationId)).thenReturn(reservationEntity);

        // when, then
        mockMvc.perform(put(RESERVATIONS.concat(UPDATE_BY_ID), reservationId)
                        .param("newDate", newDate.toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        assertEquals(newDate.toLocalDate(), reservationEntity.getDay());
        assertEquals(newDate.toLocalTime(), reservationEntity.getStartTimeR());

        verify(reservationDAO, times(1)).findEntityById(reservationId);
        verify(reservationDAO, times(1)).save(reservationEntity);
    }

    @Test
    public void testDeleteReservationById() throws Exception {
        // given
        Integer reservationId = 1;
        ReservationEntity reservationEntity = EntityFixtures.someReservation1();

        when(reservationDAO.findEntityById(reservationId)).thenReturn(reservationEntity);

        // when, then
        mockMvc.perform(delete(RESERVATIONS.concat(DELETE), reservationId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(reservationDAO, times(1)).findEntityById(reservationId);
        verify(reservationDAO, times(1)).delete(reservationEntity);
    }

}