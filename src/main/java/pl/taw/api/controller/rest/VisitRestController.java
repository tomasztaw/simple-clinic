package pl.taw.api.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;

import java.util.List;

@RestController
@RequestMapping(VisitRestController.API_VISITS)
@AllArgsConstructor
public class VisitRestController {

    public static final String API_VISITS = "/api/visits";
    public static final String VISIT_ID = "/{visitId}";

    private final VisitDAO visitDAO;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;



    @GetMapping
    public ResponseEntity<List<VisitDTO>> getAllVisits() {
        List<VisitDTO> visits = visitDAO.findAll();
        return ResponseEntity.ok(visits);
    }

    @GetMapping(VISIT_ID)
    public ResponseEntity<VisitEntity> getVisitById(@PathVariable("visitId") Integer visitId) {
        VisitEntity visit = visitDAO.findEntityById(visitId);
        return visit != null ? ResponseEntity.ok(visit) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<VisitEntity> createVisit(@RequestBody VisitEntity visitEntity) {
        VisitEntity createdVisit = visitDAO.saveAndReturn(visitEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVisit);
    }

    @PutMapping(VISIT_ID)
    public ResponseEntity<VisitEntity> updateVisit(@PathVariable("visitId") Integer visitId, @RequestBody VisitEntity updatedVisit) {
        VisitEntity existingVisit = visitDAO.findEntityById(visitId);

        if (existingVisit != null) {
            DoctorEntity doctor = null;
            if (existingVisit.getDoctor() != null) {
                doctor = doctorDAO.findEntityById(existingVisit.getDoctor().getDoctorId());
            }
            PatientEntity patient = null;
            if (existingVisit.getPatient() != null) {
                patient = patientDAO.findEntityById(existingVisit.getPatient().getPatientId());
            }

            existingVisit.setDoctor(doctor);
            existingVisit.setPatient(patient);
            existingVisit.setDateTime(updatedVisit.getDateTime());
            existingVisit.setNote(updatedVisit.getNote());
            existingVisit.setStatus(updatedVisit.getStatus());

            VisitEntity savedVisit = visitDAO.saveAndReturn(existingVisit);
            return ResponseEntity.ok(savedVisit);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(VISIT_ID)
    public ResponseEntity<Void> deleteVisit(@PathVariable("visitId") Integer visitId) {
        VisitEntity existingVisit = visitDAO.findEntityById(visitId);
        if (existingVisit != null) {
            visitDAO.delete(existingVisit);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

