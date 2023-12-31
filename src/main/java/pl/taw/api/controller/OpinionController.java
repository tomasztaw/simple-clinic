package pl.taw.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.OpinionEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(OpinionController.OPINIONS)
@AllArgsConstructor
public class OpinionController {

    public static final String OPINIONS = "/opinions";
    public static final String OPINION_ID = "/{opinionId}";
    public static final String DOCTOR_ID = "/doctor/{doctorId}";
    public static final String PATIENT_ID = "/patient/{patientId}";
    public static final String EDIT = "/{opinionId}/edit";
    public static final String PANEL = "/panel";
    public static final String ADD = "/add";
    public static final String ADD_FROM_VISIT = "/add/{visitId}/{doctorId}/{patientId}";
    public static final String SHOW = "/{opinionId}/show";
    public static final String UPDATE = "/update";
    public static final String UPDATE_BY_ID = "/{opinionId}/update";
    public static final String DELETE = "/{opinionId}/delete";

    private final OpinionDAO opinionDAO;
    private final VisitDAO visitDAO;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;

    @GetMapping(DOCTOR_ID)
    public String showDoctorOpinions(
            @PathVariable("doctorId") Integer doctorId, Model model) {
        List<OpinionDTO> opinions = opinionDAO.findAllByDoctor(doctorId);
        DoctorDTO doctor = doctorDAO.findById(doctorId);

        model.addAttribute("opinions", opinions);
        model.addAttribute("doctor", doctor);

        return "opinion/opinion-doctor-all";
    }

    @GetMapping(PATIENT_ID)
    public String showPatientOpinions(
            @PathVariable("patientId") Integer patientId, Model model) {
        List<OpinionDTO> opinions = opinionDAO.findAllByPatient(patientId);
        PatientDTO patient = patientDAO.findById(patientId);

        model.addAttribute("opinions", opinions);
        model.addAttribute("patient", patient);

        return "opinion/opinion-patient-all";
    }

    @GetMapping(PANEL)
    public String showOpinionPanel(Model model, Authentication authentication) {
        List<OpinionDTO> opinions = opinionDAO.findAll();
        model.addAttribute("opinions", opinions);
        model.addAttribute("updateOpinion", new OpinionDTO());
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("username", username);
        }
        return "opinion/opinion-panel";
    }

    @GetMapping(SHOW)
    public String showOpinion(
            @PathVariable("opinionId") Integer opinionId, Model model) {
        OpinionDTO opinion = opinionDAO.findById(opinionId);
        VisitDTO visitDTO = visitDAO.findById(opinion.getVisitId());
        model.addAttribute("opinion", opinion);
        model.addAttribute("visit", visitDTO);
        return "opinion/opinion-show";
    }

    @PostMapping(ADD)
    public String addOpinion(
            @RequestParam(value = "doctorId") Integer doctorId,
            @RequestParam(value = "patientId") Integer patientId,
            @RequestParam(value = "visitId") Integer visitId,
            @RequestParam(value = "comment") String comment,
            @RequestParam(value = "createdAt") LocalDateTime createdAt,
            HttpServletRequest request
    ) {
        VisitEntity visit = visitDAO.findEntityById(visitId); // na razie zostawię...
        OpinionEntity newOpinion = OpinionEntity.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .visitId(visitId)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();

        opinionDAO.save(newOpinion);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PostMapping(ADD_FROM_VISIT)
    public String addOpinionFromVisit(
            @PathVariable(value = "visitId") Integer visitId,
            @PathVariable(value = "doctorId") Integer doctorId,
            @PathVariable(value = "patientId") Integer patientId,
            @RequestParam(value = "comment") String comment,
            HttpServletRequest request) {
        OpinionEntity newOpinion = OpinionEntity.builder()
                .visitId(visitId)
                .doctorId(doctorId)
                .patientId(patientId)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();
        opinionDAO.save(newOpinion);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PutMapping(UPDATE)
    public String updateOpinion(
            @ModelAttribute("updateOpinion") OpinionDTO updateOpinion,
            HttpServletRequest request
    ) {
        VisitEntity visit = visitDAO.findEntityById(updateOpinion.getOpinionId());
        OpinionEntity opinionEntity = opinionDAO.findEntityById(updateOpinion.getOpinionId());
        opinionEntity.setDoctorId(updateOpinion.getDoctorId());
        opinionEntity.setPatientId(updateOpinion.getPatientId());
        opinionEntity.setVisit(visit);
        opinionEntity.setComment(updateOpinion.getComment());
        opinionEntity.setCreatedAt(updateOpinion.getCreatedAt());

        opinionDAO.save(opinionEntity);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PutMapping(UPDATE_BY_ID) // prosta aktualizacja
    public String updateOpinionById(
            @PathVariable("opinionId") Integer opinionId,
            @RequestParam String newComment,
            HttpServletRequest request
    ) {
        OpinionEntity opinion = opinionDAO.findEntityById(opinionId);
        opinion.setComment(newComment);
        opinionDAO.save(opinion);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @DeleteMapping(DELETE)
    @Transactional
    public String deleteOpinionById(@PathVariable Integer opinionId,
                                    HttpServletRequest request
    ) {
        OpinionEntity opinionForDelete = opinionDAO.findEntityById(opinionId);
        VisitEntity visit = opinionForDelete.getVisit();
        if (visit != null) {
            visit.setOpinion(null);
            visitDAO.save(visit);
        }
        opinionDAO.delete(opinionForDelete);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }


    // ####################################################################################################
    //                                      DO USUNIĘCIA ???

    // można coś pomyśleć o takiej stronie tylko z opiniami
    @GetMapping
    public String opinionsPage() {
        return "opinions";
    }


    // !!! nie działa - do wywalenia
    // dodawanie opinii przez pacjenta
//    @PostMapping(ADD)
//    @PostMapping("/add/{visitId}")
    @PostMapping(OPINIONS + ADD)
    public String addOpinion(@PathVariable("visitId") Integer visitId, Model model) {
        // Przygotowanie modelu dla formularza dodawania opinii
        OpinionDTO opinionDTO = new OpinionDTO();

        model.addAttribute("opinionDTO", opinionDTO);
        model.addAttribute("visitId", visitId);

        System.out.println("addOpinion zostało wywołane");

        return "opinion-form";
    }

}
