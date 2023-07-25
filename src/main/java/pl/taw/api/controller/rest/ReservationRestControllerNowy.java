package pl.taw.api.controller.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.hibernate.sql.ast.tree.expression.Collation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.DoctorsDTO;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.api.dto.ReservationsDTO;
import pl.taw.business.ReservationService;
import pl.taw.business.dao.ReservationDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.ReservationEntity;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(ReservationRestControllerNowy.API_RESERVATIONS)
@AllArgsConstructor
public class ReservationRestControllerNowy {

    public static final String API_RESERVATIONS = "/api/reservations/nowy";
    public static final String RESERVATION_ID = "/{reservationId}";
    public static final String BY_DATE = "/{day}";
    public static final String RESERVATION_ID_RESULT = "/%s";
    public static final String RESERVATION_UPDATE_DATE = "/{reservationId}/date";


    private final ReservationDAO reservationDAO;
    private final ReservationService reservationService;


    // start
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

    // nie wiem, czy to dobry pomysł
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





//    @GetMapping
//    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
//        List<ReservationDTO> reservations = reservationDAO.findAll();
//        return ResponseEntity.ok(reservations);
//    }
//
//    @GetMapping(BY_DATE)
//    public ResponseEntity<List<ReservationDTO>> getAllReservationsByDate(@PathVariable("day")LocalDate day) {
//        List<ReservationDTO> reservationsByDate = reservationService.findAllByDay(day);
//        if (reservationsByDate.size() > 0) {
//            return ResponseEntity.ok(reservationsByDate);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

//    @PostMapping(ADD)
//    public ResponseEntity<ReservationEntity> createReservation(@RequestBody ReservationEntity reservationEntity) {
//        ReservationEntity createdReservation = reservationDAO.saveAndReturn(reservationEntity);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
//    }

//    @PutMapping(UPDATE_BY_ID)
//    public ResponseEntity<ReservationEntity> updateReservation(
//            @PathVariable("reservationId") Integer reservationId,
//            @RequestBody ReservationEntity updateReservation) {
//
//        ReservationEntity existingReservation = reservationDAO.findEntityById(reservationId);
//
//        if (existingReservation != null) {
//            existingReservation.setDoctorId(updateReservation.getDoctorId());
//            existingReservation.setPatientId(updateReservation.getPatientId());
//            existingReservation.setDay(updateReservation.getDay());
//            existingReservation.setStartTimeR(updateReservation.getStartTimeR());
//            existingReservation.setOccupied(true);
//
//            ReservationEntity savedReservation = reservationDAO.saveAndReturn(existingReservation);
//            return ResponseEntity.ok(savedReservation);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

//    @DeleteMapping(DELETE_BY_ID)
//    public ResponseEntity<Void> deleteReservationById(@PathVariable("reservationId") Integer reservationId) {
//        ReservationEntity reservationForDelete = reservationDAO.findEntityById(reservationId);
//        if (reservationForDelete != null) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
}
