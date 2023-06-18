package pl.taw.api.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.VisitDAO;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(VisitRestController.API_VISITS)
@AllArgsConstructor
public class VisitRestController {

    public static final String API_VISITS = "/api/visits";
    public static final String VISIT_ID = "/{visitId}";

    private final VisitDAO visitDAO;



    @GetMapping
    public ResponseEntity<List<VisitDTO>> getAllVisits() {
        List<VisitDTO> visits = visitDAO.findAll();
        return ResponseEntity.ok(visits);
    }

    @GetMapping(VISIT_ID)
    public ResponseEntity<VisitDTO> getVisitById(@PathVariable("visitId") Integer visitId) {
        Optional<VisitDTO> visit = visitDAO.findById(visitId);
        return visit.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VisitDTO> createVisit(@RequestBody VisitDTO visitDTO) {
        VisitDTO createdVisit = visitDAO.save(visitDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVisit);
    }

    @PutMapping(VISIT_ID)
    public ResponseEntity<VisitDTO> updateVisit(@PathVariable("visitId") Integer visitId, @RequestBody VisitDTO updatedVisit) {
        Optional<VisitDTO> existingVisit = visitDAO.findById(visitId);
        if (existingVisit.isPresent()) {
            VisitDTO existingVisitData = existingVisit.get();
            VisitDTO updatedVisitWithId = VisitDTO.builder()
                    .visitId(existingVisitData.getVisitId())
                    .doctorId(updatedVisit.getDoctorId())
                    .patientId(updatedVisit.getPatientId())
                    .dateTime(updatedVisit.getDateTime())
                    .note(updatedVisit.getNote())
                    .status(updatedVisit.getStatus())
                    .build();

            VisitDTO savedVisit = visitDAO.save(updatedVisitWithId);
            return ResponseEntity.ok(savedVisit);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(VISIT_ID)
    public ResponseEntity<Void> deleteVisit(@PathVariable("visitId") Integer visitId) {
        Optional<VisitDTO> existingVisit = visitDAO.findById(visitId);
        if (existingVisit.isPresent()) {
            visitDAO.delete(existingVisit.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

