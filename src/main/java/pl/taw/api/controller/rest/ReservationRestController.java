package pl.taw.api.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.business.ReservationService;
import pl.taw.business.dao.ReservationDAO;
import pl.taw.infrastructure.database.entity.ReservationEntity;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(ReservationRestController.API_RESERVATIONS)
@AllArgsConstructor
public class ReservationRestController {

    public static final String API_RESERVATIONS = "/api/reservations";
    public static final String BY_DATE = "/{day}";
    public static final String UPDATE_BY_ID = "/update/{opinionId}";
    public static final String ADD = "/add";
    public static final String DELETE_BY_ID = "/delete/{opinionId}";


    private final ReservationDAO reservationDAO;
    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationDAO.findAll();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping(BY_DATE)
    public ResponseEntity<List<ReservationDTO>> getAllReservationsByDate(@PathVariable("day")LocalDate day) {
        List<ReservationDTO> reservationsByDate = reservationService.findAllByDay(day);
        if (reservationsByDate.size() > 0) {
            return ResponseEntity.ok(reservationsByDate);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(ADD)
    public ResponseEntity<ReservationEntity> createReservation(@RequestBody ReservationEntity reservationEntity) {
        ReservationEntity createdReservation = reservationDAO.saveAndReturn(reservationEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    @PutMapping(UPDATE_BY_ID)
    public ResponseEntity<ReservationEntity> updateReservation(
            @PathVariable("reservationId") Integer reservationId,
            @RequestBody ReservationEntity updateReservation) {

        ReservationEntity existingReservation = reservationDAO.findEntityById(reservationId);

        if (existingReservation != null) {
            existingReservation.setDoctorId(updateReservation.getDoctorId());
            existingReservation.setPatientId(updateReservation.getPatientId());
            existingReservation.setDay(updateReservation.getDay());
            existingReservation.setStartTimeR(updateReservation.getStartTimeR());
            existingReservation.setOccupied(true);

            ReservationEntity savedReservation = reservationDAO.saveAndReturn(existingReservation);
            return ResponseEntity.ok(savedReservation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(DELETE_BY_ID)
    public ResponseEntity<Void> deleteReservationById(@PathVariable("reservationId") Integer reservationId) {
        ReservationEntity reservationForDelete = reservationDAO.findEntityById(reservationId);
        if (reservationForDelete != null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
