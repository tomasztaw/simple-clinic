package pl.taw.api.controller.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.api.dto.ReservationsDTO;
import pl.taw.business.ReservationService;
import pl.taw.business.dao.ReservationDAO;
import pl.taw.infrastructure.database.entity.ReservationEntity;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping(ReservationRestController.API_RESERVATIONS)
@AllArgsConstructor
public class ReservationRestController {

    public static final String API_RESERVATIONS = "/api/reservations";
    public static final String RESERVATION_ID = "/{reservationId}";
    public static final String BY_DATE = "/{day}";
    public static final String RESERVATION_ID_RESULT = "/%s";
    public static final String RESERVATION_UPDATE_DATE = "/{reservationId}/date";


    private final ReservationDAO reservationDAO;
    private final ReservationService reservationService;


    @GetMapping
    public ReservationsDTO getReservationsAsList() {
        return ReservationsDTO.of(reservationDAO.findAll());
    }

    @GetMapping(value = RESERVATION_ID, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ReservationDTO> reservationDetails(
            @PathVariable("reservationId") Integer reservationId
    ) {
        ReservationDTO reservation = reservationDAO.findById(reservationId);

        if (reservation != null) {
            return ResponseEntity.ok(reservation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(BY_DATE)
    public ResponseEntity<ReservationsDTO> getAllReservationsByDate(
            @PathVariable("day") String dayString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate day = LocalDate.parse(dayString, formatter);

            List<ReservationDTO> reservations = reservationService.findAllByDay(day);
            if (reservations.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                ReservationsDTO reservationsByDate = ReservationsDTO.of(reservations);
                return ResponseEntity.ok(reservationsByDate);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping
    @Transactional
    public ResponseEntity<ReservationDTO> addReservation(
            @Valid @RequestBody ReservationDTO reservationDTO
    ) {
        ReservationEntity reservationEntity = ReservationEntity.builder()
                .doctorId(reservationDTO.getDoctorId())
                .patientId(reservationDTO.getPatientId())
                .day(reservationDTO.getDay())
                .startTimeR(reservationDTO.getStartTimeR())
                .occupied(reservationDTO.getOccupied())
                .build();

        ReservationEntity created = reservationDAO.saveAndReturn(reservationEntity);

        return ResponseEntity
                .created(URI.create(API_RESERVATIONS + RESERVATION_ID_RESULT.formatted(created.getReservationId())))
                .build();
    }

    @PutMapping(RESERVATION_ID)
    @Transactional
    public ResponseEntity<?> updateReservation(
            @PathVariable("reservationId") Integer reservationId,
            @Valid @RequestBody ReservationDTO reservationDTO
    ) {
        ReservationEntity existingReservation = reservationDAO.findEntityById(reservationId);

        existingReservation.setDoctorId(reservationDTO.getDoctorId());
        existingReservation.setPatientId(reservationDTO.getPatientId());
        existingReservation.setDay(reservationDTO.getDay());
        existingReservation.setStartTimeR(reservationDTO.getStartTimeR());
        existingReservation.setOccupied(reservationDTO.getOccupied());

        reservationDAO.save(existingReservation);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(RESERVATION_ID)
    public ResponseEntity<?> deleteReservation(
            @PathVariable("reservationId") Integer reservationId
    ) {
        ReservationEntity existingReservation = reservationDAO.findEntityById(reservationId);
        if (existingReservation != null) {
            reservationDAO.delete(existingReservation);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping(RESERVATION_UPDATE_DATE)
    public ResponseEntity<?> updateReservationDate( // TODO można tutaj zwrócić wiadomość z nową datą
            @PathVariable("reservationId") Integer reservationId,
            @RequestParam String dateTime
    ) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime ldt = LocalDateTime.parse(dateTime, formatter);

            ReservationEntity existingReservation = reservationDAO.findEntityById(reservationId);

            existingReservation.setDay(ldt.toLocalDate());
            existingReservation.setStartTimeR(ldt.toLocalTime());

            reservationDAO.save(existingReservation);

            return ResponseEntity.ok().build();

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use 'yyyy-MM-dd HH:mm' format.");
        }

    }
}
