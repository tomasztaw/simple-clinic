package pl.taw.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public static final String DOCTOR_ID = "/doctor/{doctorId}";
    public static final String PATIENT_ID = "/patient/{patientId}";
    public static final String EDIT = "/edit/{opinionId}";
    public static final String PANEL = "/panel";
    public static final String ADD = "/add";
    public static final String SHOW = "/show/{opinionId}";
    public static final String UPDATE = "/update";
    public static final String UPDATE_BY_ID = "/update/{opinionId}";
    public static final String DELETE = "/delete/{opinionId}";


    private final OpinionDAO opinionDAO;
    private final VisitDAO visitDAO;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;

    // panel
    @GetMapping(PANEL)
    public String showOpinionPanel(Model model) {
        List<OpinionDTO> opinions = opinionDAO.findAll();
        model.addAttribute("opinions", opinions);
        model.addAttribute("updateOpinion", new OpinionDTO());
        return "opinion/opinion-panel";
    }

    @GetMapping(SHOW)
    public String showOpinion(
            @PathVariable("opinionId") Integer opinionId, Model model) {
        OpinionDTO opinion = opinionDAO.findById(opinionId);
        model.addAttribute("opinion", opinion);
        // dodawanie modelu z wizytą
        VisitDTO visit = visitDAO.findById(opinion.getVisit().getVisitId());
        model.addAttribute("visit", visit);
        return "opinion/opinion-show";
    }

    @PostMapping(ADD)
    public String addOpinion(
            @RequestParam(value = "doctorId") Integer doctorId,
            @RequestParam(value = "patientId") Integer patientId,
            @RequestParam(value = "visitId") Integer visitId,
            @RequestParam(value = "comment") String comment,
            @RequestParam(value = "createdAt") LocalDateTime createdAt) {
        VisitEntity visit = visitDAO.findEntityById(visitId);
        OpinionEntity newOpinion = OpinionEntity.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .visit(visit)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();

        opinionDAO.save(newOpinion);
        // TODO zrobić przekierowanie zależne od panelu admin/pacjent
        return "redirect:/opinions/opinion-panel";
    }

    @PutMapping(UPDATE)
    public String updateOpinion(
            @ModelAttribute("updateOpinion") OpinionDTO updateOpinion) {
        VisitEntity visit = visitDAO.findEntityById(updateOpinion.getOpinionId());
        OpinionEntity opinionEntity = opinionDAO.findEntityById(updateOpinion.getOpinionId());
        opinionEntity.setDoctorId(updateOpinion.getDoctor().getDoctorId());
        opinionEntity.setPatientId(updateOpinion.getPatient().getPatientId());
        opinionEntity.setVisit(visit);
        opinionEntity.setComment(updateOpinion.getComment());
        opinionEntity.setCreatedAt(updateOpinion.getCreatedAt());

        opinionDAO.save(opinionEntity);
        // TODO zrobić przekierowanie zależne od panelu admin/pacjent
        return "redirect:/opinions/opinion-panel";
    }

    @PutMapping(UPDATE_BY_ID) // prosta aktualizacja
    public String updateOpinionById(
            @PathVariable("opinionId") Integer opinionId,
            @RequestParam String newComment,
            HttpServletRequest request) {
        OpinionEntity opinion = opinionDAO.findEntityById(opinionId);
        opinion.setComment(newComment);
        opinionDAO.save(opinion);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @DeleteMapping(DELETE)
    public String deleteOpinionById(@PathVariable Integer opinionId) {
        OpinionEntity opinionForDelete = opinionDAO.findEntityById(opinionId);
        opinionDAO.delete(opinionForDelete);

        return "redirect:/opinions/opinion-panel";
    }


    // ####################################################################################################

    @GetMapping
    public String opinionsPage() {
        return "opinions";
    }

    // wyświetlanie opinii lekarza
    @GetMapping(DOCTOR_ID)
    public String showDoctorOpinions(
            @PathVariable("doctorId") Integer doctorId, Model model) {
        List<OpinionDTO> opinions = opinionDAO.findAllByDoctor(doctorId);
        DoctorDTO doctor = doctorDAO.findById(doctorId);

        model.addAttribute("opinions", opinions);
        model.addAttribute("doctor", doctor);

        return "opinion/opinion-doctor-all";
    }

    // wyświetlanie opinii konkretnego pacjenta
    @GetMapping(PATIENT_ID)
    public String showPatientOpinions(
            @PathVariable("patientId") Integer patientId, Model model) {
        List<OpinionDTO> opinions = opinionDAO.findAllByPatient(patientId);
        PatientDTO patient = patientDAO.findById(patientId);

        model.addAttribute("opinions", opinions);
        model.addAttribute("patient", patient);

        return "opinion/opinion-patient-all";
    }

    // !!! nie działa
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

        return "opinion-form"; // Nazwa widoku z formularzem dodawania opinii
    }


    // wzór dla posta
    @PostMapping("/opinions/addNieMaTakiego")
    public String addOpinion(
            @ModelAttribute("opinionDTO") OpinionDTO opinionDTO,
            @RequestParam("visitId") Integer visitId) {
        // Przekształcenie visitId na obiekt VisitEntity
        VisitDTO visit = new VisitDTO();
        visit.setVisitId(visitId);
        opinionDTO.setVisit(visit);

        // Dodanie opinii do bazy danych

        return "redirect:/opinions";
    }


}