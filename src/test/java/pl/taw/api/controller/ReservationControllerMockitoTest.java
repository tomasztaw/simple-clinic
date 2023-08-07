package pl.taw.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationControllerMockitoTest {

    @Mock
    private ReservationDAO reservationDAO;
    @Mock
    private DoctorDAO doctorDAO;
    @Mock
    private PatientDAO patientDAO;
    @Mock
    private Model model;
    @Mock
    private Authentication authentication;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ReservationController reservationController;


    @Test
    void testShowReservationPanel() {
        // given
        List<ReservationDTO> reservations = DtoFixtures.reservations;

        when(reservationDAO.findAll()).thenReturn(reservations);
        when(authentication.getName()).thenReturn("testUsername");

        // when
        String result = reservationController.showReservationPanel(model, authentication);

        // then
        assertEquals("reservation/reservation-panel", result);

        verify(reservationDAO, times(1)).findAll();
        verify(model).addAttribute("reservations", reservations);
        verify(model).addAttribute(eq("updateReservation"), any(ReservationDTO.class));
        verify(model).addAttribute("username", "testUsername");
    }

    @Test
    void testShowReservation() {
        // given
        Integer reservationId = 3;
        ReservationDTO reservationDTO = DtoFixtures.someReservation3();

        DoctorDTO doctorDTO = DtoFixtures.someDoctor3();
        PatientDTO patientDTO = DtoFixtures.somePatient2();

        when(reservationDAO.findById(reservationId)).thenReturn(reservationDTO);
        when(doctorDAO.findById(reservationDTO.getDoctorId())).thenReturn(doctorDTO);
        when(patientDAO.findById(reservationDTO.getPatientId())).thenReturn(patientDTO);

        // when
        String result = reservationController.showReservation(reservationId, model);

        // then
        assertEquals("reservation/reservation-show", result);

        verify(reservationDAO, times(1)).findById(reservationId);
        verify(doctorDAO, times(1)).findById(reservationDTO.getDoctorId());
        verify(patientDAO, times(1)).findById(reservationDTO.getPatientId());

        verify(model).addAttribute("reservation", reservationDTO);
        verify(model).addAttribute("doctor", doctorDTO);
        verify(model).addAttribute("patient", patientDTO);
    }

    @Test
    void testShowDoctorReservations() {
        // given
        Integer doctorId = 100;
        List<ReservationDTO> reservationList = DtoFixtures.reservations.stream().map(res -> res.withDoctorId(doctorId)).toList();
        DoctorDTO doctorDTO = DtoFixtures.someDoctor3().withDoctorId(doctorId);

        when(reservationDAO.findAllByDoctor(doctorId)).thenReturn(reservationList);
        when(doctorDAO.findById(doctorId)).thenReturn(doctorDTO);

        // when
        String result = reservationController.showDoctorReservations(doctorId, model);

        // then
        assertEquals("reservation/reservation-doctor-all", result);

        verify(reservationDAO, times(1)).findAllByDoctor(doctorId);
        verify(doctorDAO, times(1)).findById(doctorId);

        verify(model).addAttribute("reservations", reservationList);
        verify(model).addAttribute("doctor", doctorDTO);
    }

    @Test
    void testShowPatientReservations() {
        // given
        Integer patientId = 50;
        List<ReservationDTO> reservationList = DtoFixtures.reservations.stream().map(res -> res.withPatientId(patientId)).toList();
        PatientDTO patientDTO = DtoFixtures.somePatient2().withPatientId(patientId);

        when(reservationDAO.findAllByPatient(patientId)).thenReturn(reservationList);
        when(patientDAO.findById(patientId)).thenReturn(patientDTO);

        // when
        String result = reservationController.showPatientReservations(patientId, model);

        // then
        assertEquals("reservation/reservation-patient-all", result);

        verify(reservationDAO, times(1)).findAllByPatient(patientId);
        verify(patientDAO, times(1)).findById(patientId);

        verify(model).addAttribute("reservations", reservationList);
        verify(model).addAttribute("patient", patientDTO);
    }

    @Test
    void testConfirmReservation() {
        // given
        Integer reservationId = 1;
        LocalDateTime fullDate = LocalDateTime.of(2023, 8, 7, 12, 10);

        // when
        String result = reservationController.confirmReservation(reservationId, fullDate, model);

        // then
        assertEquals("reservation-confirm", result);

        verify(model).addAttribute("reservationId", reservationId);
        verify(model).addAttribute("fullDate", fullDate);

    }

    @Test
    void testAddReservation() {
        // given
        Integer doctorId = 1;
        Integer patientId = 2;
        LocalDate day = LocalDate.of(2023, 8, 7);
        LocalTime startTimeR = LocalTime.of(10, 20);
        String referer = request.getHeader("Referer");
        String expectedRedirect = "redirect:" + referer;

        // when
        String result = reservationController.addReservation(doctorId, patientId, day, startTimeR, request);

        // then
        assertEquals(expectedRedirect, result);

        verify(reservationDAO, times(1)).save(any(ReservationEntity.class));
    }

    @Test
    void testCreateReservation() {
        // given
        Integer reservationId = 15;
        Integer doctorId = 1;
        Integer patientId = 2;
        String day = "2023-08-07";
        String startTimeR = "10:50";
        LocalDate date = LocalDate.parse(day);
        LocalTime startTime = LocalTime.parse(startTimeR, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime fullDate = LocalDateTime.of(date, startTime);
        ReservationEntity reservationEntity = EntityFixtures.someReservation1();
        ReservationEntity savedReservation = reservationEntity.withReservationId(reservationId);
        String expectedRedirect = "redirect:/reservations/confirm?reservationId=" +
                savedReservation.getReservationId() +
                "&fullDate=" + fullDate;

        when(reservationDAO.saveAndReturn(any(ReservationEntity.class))).thenReturn(savedReservation);

        // when
        String result = reservationController.createReservation(doctorId, patientId, day, startTimeR);

        // then
        assertEquals(expectedRedirect, result);

        verify(reservationDAO, times(1)).saveAndReturn(any(ReservationEntity.class));
    }

    @Test
    public void testUpdateReservation() {
        // given
        Integer reservationId = 1;
        ReservationEntity reservationEntity = EntityFixtures.someReservation1();
        ReservationDTO updateReservation = DtoFixtures.someReservation1();
        String referer = request.getHeader("Referer");

        when(reservationDAO.findEntityById(reservationId)).thenReturn(reservationEntity);

        // when
        String result = reservationController.updateReservation(updateReservation, request);

        // then
        assertEquals("redirect:" + referer, result);
        assertTrue(reservationEntity.getOccupied());

        verify(reservationDAO, times(1)).findEntityById(reservationId);
        verify(reservationDAO, times(1)).save(reservationEntity);
    }

    @Test
    public void testUpdateReservationTimeById() {
        // given
        Integer reservationId = 2;
        ReservationEntity reservationEntity = EntityFixtures.someReservation2();
        String referer = request.getHeader("Referer");
        LocalDateTime newDateTime = LocalDateTime.of(2023, 8, 7, 14, 10);

        when(reservationDAO.findEntityById(anyInt())).thenReturn(reservationEntity);

        // when
        String result = reservationController.updateReservationTimeById(reservationId, newDateTime, request);

        // then
        assertEquals("redirect:" + referer, result);
        assertEquals(newDateTime.toLocalDate(), reservationEntity.getDay());
        assertEquals(newDateTime.toLocalTime(), reservationEntity.getStartTimeR());

        verify(reservationDAO, times(1)).findEntityById(reservationId);
        verify(reservationDAO, times(1)).save(reservationEntity);
    }

    @Test
    public void testDeleteReservationById() {
        // given
        ReservationEntity reservationEntity = EntityFixtures.someReservation3();
        String referer = request.getHeader("Referer");

        when(reservationDAO.findEntityById(anyInt())).thenReturn(reservationEntity);

        // when
        String result = reservationController.deleteReservationById(3, request);

        // then
        assertEquals("redirect:" + referer, result);

        verify(reservationDAO, times(1)).findEntityById(3);
        verify(reservationDAO, times(1)).delete(reservationEntity);
    }

    @Test
    public void testReservationPage() {
        // given, when
        String result = reservationController.reservationPage();

        // then
        assertEquals("reservations", result);
    }

    @Test
    void testCalendar() {
        // given, when
        String result = reservationController.calendar();

        // then
        assertEquals("calendar-2", result);
    }


}