package pl.taw.api.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.VisitEntity;

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
    public ResponseEntity<List<VisitEntity>> getAllVisits() {
        List<VisitEntity> visits = visitDAO.findAll();
        return ResponseEntity.ok(visits);
    }

    @GetMapping(VISIT_ID)
    public ResponseEntity<VisitEntity> getVisitById(@PathVariable("visitId") Integer visitId) {
        Optional<VisitEntity> visit = visitDAO.findEntityById(visitId);
        return visit.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VisitEntity> createVisit(@RequestBody VisitEntity visitEntity) {
        VisitEntity createdVisit = visitDAO.save(visitEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVisit);
    }

    @PutMapping(VISIT_ID)
    public ResponseEntity<VisitEntity> updateVisit(@PathVariable("visitId") Integer visitId, @RequestBody VisitEntity updatedVisit) {
        Optional<VisitEntity> existingVisit = visitDAO.findEntityById(visitId);
        if (existingVisit.isPresent()) {
            VisitEntity existingVisitData = existingVisit.get();
            VisitEntity updatedVisitWithId = VisitEntity.builder()
                    .visitId(existingVisitData.getVisitId())
                    .doctorId(updatedVisit.getDoctorId())
                    .patientId(updatedVisit.getPatientId())
                    .dateTime(updatedVisit.getDateTime())
                    .note(updatedVisit.getNote())
                    .status(updatedVisit.getStatus())
                    .build();

            VisitEntity savedVisit = visitDAO.save(updatedVisitWithId);
            return ResponseEntity.ok(savedVisit);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(VISIT_ID)
    public ResponseEntity<Void> deleteVisit(@PathVariable("visitId") Integer visitId) {
        Optional<VisitEntity> existingVisit = visitDAO.findEntityById(visitId);
        if (existingVisit.isPresent()) {
            visitDAO.delete(existingVisit.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

