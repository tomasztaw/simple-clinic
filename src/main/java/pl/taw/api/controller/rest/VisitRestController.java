package pl.taw.api.controller.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.VisitDTO;
import pl.taw.api.dto.VisitsDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;

import java.net.URI;

@RestController
@RequestMapping(VisitRestController.API_VISITS)
@AllArgsConstructor
public class VisitRestController {

    public static final String API_VISITS = "/api/visits";
    public static final String VISIT_ID = "/{visitId}";
    public static final String VISIT_ID_RESULT = "/%s";
    public static final String VISIT_UPDATE_NOTE = "/{visitId}/note";


    private final VisitDAO visitDAO;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;


    @GetMapping
    public VisitsDTO getAllVisits() {
        return VisitsDTO.of(visitDAO.findAll());
    }

    @GetMapping(VISIT_ID)
    public VisitDTO visitDetails(@PathVariable("visitId") Integer visitId) {
        return visitDAO.findById(visitId);
    }


    // TODO jest problem z metodą saveAndReturn
    @PostMapping
    @Transactional
    public ResponseEntity<VisitDTO> addVisit(
            @Valid @RequestBody VisitDTO visitDTO
    ) {
        VisitEntity visitEntity = VisitEntity.builder()
                .doctorId(visitDTO.getDoctorId())
                .patientId(visitDTO.getPatientId())
                .note(visitDTO.getNote())
                .dateTime(visitDTO.getDateTime())
                .status(visitDTO.getStatus())
                .build();

        VisitEntity createdVisit = visitDAO.saveAndReturn(visitEntity);

        return ResponseEntity
                .created(URI.create(API_VISITS + VISIT_ID_RESULT.formatted(createdVisit.getVisitId())))
                .build();
    }


    @PutMapping(VISIT_ID)
    public ResponseEntity<VisitEntity> updateVisit(
            @PathVariable("visitId") Integer visitId,
            @Valid @RequestBody VisitDTO updatedVisit
    ) {
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

            visitDAO.save(existingVisit);
            return ResponseEntity.ok(existingVisit);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping(VISIT_ID)
    public ResponseEntity<?> deleteVisit(
            @PathVariable("visitId") Integer visitId
    ) {
        VisitEntity existingVisit = visitDAO.findEntityById(visitId);

        if (existingVisit == null) {
            return ResponseEntity.notFound().build();
        }

        visitDAO.delete(existingVisit);

        return ResponseEntity.noContent().build();
    }


    @PatchMapping(VISIT_UPDATE_NOTE)
    public ResponseEntity<?> updateVisitNote(
            @PathVariable("visitId") Integer visitId,
            @RequestParam(required = true) String updatedNote
    ) {
        VisitEntity existingVisit = visitDAO.findEntityById(visitId);

        existingVisit.setNote(updatedNote);

        visitDAO.save(existingVisit);

        return ResponseEntity.ok().build();
    }

}

