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
import java.util.Collections;

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
    public ReservationDTO reservationDetails(
            @PathVariable("reservationId") Integer reservationId
    ) {
        return reservationDAO.findById(reservationId);
    }

    @GetMapping(BY_DATE)
    public ReservationsDTO getAllReservationsByDate(@PathVariable("day") LocalDate day) {
        ReservationsDTO reservationsByDate = ReservationsDTO.of(reservationService.findAllByDay(day));
        if (reservationsByDate.getReservations().size() > 0) {
            return reservationsByDate;
        } else {
            return ReservationsDTO.of(Collections.emptyList());
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
                .created(URI.create(RESERVATION_ID.concat(RESERVATION_ID_RESULT).formatted(created.getReservationId())))
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
        reservationDAO.delete(existingReservation);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(RESERVATION_UPDATE_DATE)
    public ResponseEntity<?> updateReservationDate(
            @PathVariable("reservationId") Integer reservationId,
            @RequestParam(required = true) LocalDateTime dateTime
    ) {
        ReservationEntity existingReservation = reservationDAO.findEntityById(reservationId);

        existingReservation.setDay(dateTime.toLocalDate());
        existingReservation.setStartTimeR(dateTime.toLocalTime());

        reservationDAO.save(existingReservation);

        return ResponseEntity.ok().build();
    }
}
