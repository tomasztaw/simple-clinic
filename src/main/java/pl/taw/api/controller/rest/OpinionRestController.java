package pl.taw.api.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.api.dto.OpinionsDTO;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.infrastructure.database.entity.OpinionEntity;

import java.util.List;

@RestController
@RequestMapping(OpinionRestController.API_OPINIONS)
@AllArgsConstructor
public class OpinionRestController {

    public static final String API_OPINIONS = "/api/opinions";  // było "/api/opinions/nowy" - jak wszystko będzie ok to usunę
    public static final String OPINION_ID = "/{opinionId}";
    public static final String COMMENTS = "/comments";
    public static final String UPDATE_BY_ID = "/{opinionId}/update";
    public static final String DELETE_BY_ID = "/{opinionId}/delete";
    public static final String OPINION_UPDATE_NOTE = "/{opinionId}/comment";


    private final OpinionDAO opinionDAO;

    @GetMapping
    public OpinionsDTO getOpinions() {
        return OpinionsDTO.of(opinionDAO.findAll());
    }

    @GetMapping(value = OPINION_ID, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OpinionDTO getOpinionDetails(@PathVariable("opinionId") Integer opinionId) {
        return opinionDAO.findById(opinionId);
    }

    @GetMapping(value = COMMENTS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllOpinionComments() {
        List<String> comments = opinionDAO.findAll().stream()
                .map(OpinionDTO::getComment)
                .toList();
        return ResponseEntity.ok(comments);
    }

    @PostMapping
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

    @PatchMapping(OPINION_UPDATE_NOTE)
    public ResponseEntity<?> updateOpinionComment(
            @PathVariable("opinionId") Integer opinionId,
            @RequestParam(required = true) String updatedComment
    ) {
        OpinionEntity existingOpinion = opinionDAO.findEntityById(opinionId);
        existingOpinion.setComment(updatedComment);
        opinionDAO.save(existingOpinion);

        return ResponseEntity.ok().build();
    }
}
