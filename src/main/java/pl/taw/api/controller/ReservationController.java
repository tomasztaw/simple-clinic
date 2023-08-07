package pl.taw.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.*;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.business.dao.ReservationDAO;
import pl.taw.infrastructure.database.entity.ReservationEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping(ReservationController.RESERVATIONS)
@AllArgsConstructor
public class ReservationController {

    public static final String RESERVATIONS = "/reservations";
    public static final String CALENDAR = "/calendar";
    public static final String ADD_PARAMS = "/add/{doctorId}/{patientId}/{day}/{startTimeR}";
    public static final String SHOW = "/show/{reservationId}";
    public static final String ADD = "/add";
    public static final String UPDATE = "/update";
    public static final String UPDATE_BY_ID = "/update/{reservationId}";
    public static final String DELETE = "/delete/{reservationId}";
    public static final String PANEL = "/panel";
    public static final String CONFIRM = "/confirm";
    public static final String DOCTOR_ID = "/doctor/{doctorId}";
    public static final String PATIENT_ID = "/patient/{patientId}";


    private final ReservationDAO reservationDAO;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;

    @GetMapping(PANEL)
    public String showReservationPanel(Model model, Authentication authentication) {

        List<ReservationDTO> reservations = reservationDAO.findAll();
        model.addAttribute("reservations", reservations);
        model.addAttribute("updateReservation", new ReservationDTO());
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("username", username);
        }

        return "reservation/reservation-panel";
    }

    @GetMapping(SHOW)
    public String showReservation(@PathVariable("reservationId") Integer reservationId, Model model) {

        ReservationDTO reservation = reservationDAO.findById(reservationId);
        DoctorDTO doctor = doctorDAO.findById(reservation.getDoctorId());
        PatientDTO patient = patientDAO.findById(reservation.getPatientId());

        model.addAttribute("reservation", reservation);
        model.addAttribute("doctor", doctor);
        model.addAttribute("patient", patient);

        return "reservation/reservation-show";
    }

    @GetMapping(DOCTOR_ID)
    public String showDoctorReservations(
            @PathVariable("doctorId") Integer doctorId, Model model) {
        List<ReservationDTO> reservations = reservationDAO.findAllByDoctor(doctorId);
        DoctorDTO doctor = doctorDAO.findById(doctorId);

        model.addAttribute("reservations", reservations);
        model.addAttribute("doctor", doctor);

        return "reservation/reservation-doctor-all";
    }

    @GetMapping(PATIENT_ID)
    public String showPatientReservations(
            @PathVariable("patientId") Integer patientId, Model model) {

        List<ReservationDTO> reservations = reservationDAO.findAllByPatient(patientId);
        PatientDTO patient = patientDAO.findById(patientId);

        model.addAttribute("reservations", reservations);
        model.addAttribute("patient", patient);

        return "reservation/reservation-patient-all";
    }

    @GetMapping(CONFIRM)
    public String confirmReservation(
            @RequestParam("reservationId") Integer reservationId,
            @RequestParam("fullDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime fullDate,
            Model model
    ) {
        model.addAttribute("reservationId", reservationId);
        model.addAttribute("fullDate", fullDate);

        return "reservation-confirm";
    }

    @GetMapping
    public String reservationPage() {
        return "reservations";
    }

    @GetMapping(CALENDAR)
    public String calendar() {
        return "calendar-2";
    }



    @PostMapping(ADD)
    public String addReservation(
            @RequestParam(value = "doctorId") Integer doctorId,
            @RequestParam(value = "patientId") Integer patientId,
            @RequestParam(value = "day") LocalDate day,
            @RequestParam(value = "startTimeR") LocalTime startTimeR,
            HttpServletRequest request
    ) {
        ReservationEntity newReservation = ReservationEntity.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .day(day)
                .startTimeR(startTimeR)
                .occupied(true)
                .build();

        reservationDAO.save(newReservation);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PostMapping(ADD_PARAMS)
    public String createReservation(
            @PathVariable("doctorId") Integer doctorId,
            @PathVariable("patientId") Integer patientId,
            @PathVariable("day") String day,
            @PathVariable("startTimeR") String startTimeR) {
        LocalDate date = LocalDate.parse(day);
        LocalTime startTime = LocalTime.parse(startTimeR, DateTimeFormatter.ofPattern("HH:mm"));

        LocalDateTime fullDate = LocalDateTime.of(date, startTime);

        ReservationEntity reservation = ReservationEntity.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .day(date)
                .startTimeR(startTime)
                .occupied(true)
                .build();

        ReservationEntity savedReservation = reservationDAO.saveAndReturn(reservation);

        return "redirect:/reservations/confirm?reservationId=" + savedReservation.getReservationId() +
                "&fullDate=" + fullDate;
    }



    @PutMapping(UPDATE)
    public String updateReservation(
            @ModelAttribute("updateReservation") ReservationDTO updateReservation,
            HttpServletRequest request
    ) {
        ReservationEntity reservation = reservationDAO.findEntityById(updateReservation.getReservationId());
        reservation.setDoctorId(updateReservation.getDoctorId());
        reservation.setPatientId(updateReservation.getPatientId());
        reservation.setDay(updateReservation.getDay());
        reservation.setStartTimeR(updateReservation.getStartTimeR());
        reservation.setOccupied(true);

        reservationDAO.save(reservation);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PutMapping(UPDATE_BY_ID) // prosta aktualizacja, dzień i godzina
    public String updateReservationTimeById(
            @PathVariable("reservationId") Integer reservationId,
            @RequestParam LocalDateTime newDate,
            HttpServletRequest request) {

        ReservationEntity reservation = reservationDAO.findEntityById(reservationId);
        reservation.setDay(newDate.toLocalDate());
        reservation.setStartTimeR(newDate.toLocalTime());

        reservationDAO.save(reservation);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }


    @DeleteMapping(DELETE)
    public String deleteReservationById(@PathVariable Integer reservationId, HttpServletRequest request) {
        ReservationEntity reservationForDelete = reservationDAO.findEntityById(reservationId);
        reservationDAO.delete(reservationForDelete);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }






}
