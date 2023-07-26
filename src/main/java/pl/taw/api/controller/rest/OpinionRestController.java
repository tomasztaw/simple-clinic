package pl.taw.api.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.database.entity.OpinionEntity;

import java.util.List;

@RestController
@RequestMapping(OpinionRestController.API_OPINIONS)
@AllArgsConstructor
public class OpinionRestController {

    public static final String API_OPINIONS = "/api/opinions";
    public static final String COMMENTS = "/comments";

    public static final String UPDATE_BY_ID = "/update/{opinionId}";
    public static final String ADD = "/add";
    public static final String DELETE_BY_ID = "/delete/{opinionId}";

    private final OpinionDAO opinionDAO;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;

    @GetMapping
    public ResponseEntity<List<OpinionDTO>> getAllOpinions() {
        List<OpinionDTO> opinions = opinionDAO.findAll();
        return ResponseEntity.ok(opinions);
    }

    @GetMapping(value = COMMENTS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllOpinionComments() {
        List<String> comments = opinionDAO.findAll().stream()
                .map(OpinionDTO::getComment)
                .toList();
        return ResponseEntity.ok(comments);
    }

    // to na pewno źle !!!!!!!!!!!!!
    @GetMapping(value = UPDATE_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpinionDTO> getOpinionById(@PathVariable("opinionId") Integer opinionId) {
        OpinionDTO opinion = opinionDAO.findById(opinionId);
        if (opinion != null) {
            return ResponseEntity.ok(opinion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(ADD)
    public ResponseEntity<OpinionEntity> createOpinion(@RequestBody OpinionEntity opinionEntity) {
        OpinionEntity createdOpinion = opinionDAO.saveAndReturn(opinionEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOpinion);
    }

    @PutMapping(UPDATE_BY_ID)
    public ResponseEntity<OpinionEntity> updateOpinion(
            @PathVariable("opinionId") Integer opinionId,
            @RequestBody OpinionEntity updateOpinion) {

        OpinionEntity existingOpinion = opinionDAO.findEntityById(opinionId);

        if (existingOpinion != null) {
            existingOpinion.setDoctorId(updateOpinion.getDoctorId());
            existingOpinion.setPatientId(updateOpinion.getPatientId());
            existingOpinion.setVisitId(updateOpinion.getVisitId());
            existingOpinion.setComment(updateOpinion.getComment());
            existingOpinion.setCreatedAt(updateOpinion.getCreatedAt());

            OpinionEntity savedOpinion = opinionDAO.saveAndReturn(existingOpinion);
            return ResponseEntity.ok(savedOpinion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping(DELETE_BY_ID)
    public ResponseEntity<Void> deleteOpinionById(@PathVariable("opinionId") Integer opinionId) {
        OpinionEntity opinionForDelete = opinionDAO.findEntityById(opinionId);
        if (opinionForDelete != null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
