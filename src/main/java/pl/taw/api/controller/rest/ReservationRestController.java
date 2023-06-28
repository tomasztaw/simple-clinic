package pl.taw.api.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.business.ReservationService;
import pl.taw.business.dao.ReservationDAO;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(ReservationRestController.API_RESERVATIONS)
@AllArgsConstructor
public class ReservationRestController {

    public static final String API_RESERVATIONS = "/api/reservations";
    public static final String BY_DATE = "/{day}";

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

}
