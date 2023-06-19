package pl.taw.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.taw.api.dto.AlterVisitDTO;
import pl.taw.business.dao.AlterVisitDAO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.infrastructure.database.repository.mapper.VisitMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(VisitController.VISITS)
@AllArgsConstructor
public class VisitController {

    public static final String VISITS = "/visits";
    public static final String PANEL = "/panel";
    public static final String SHOW = "/show/{visitId}";

    public static final String VISIT_ID = "/{visitId}";
    public static final String ADD = "/add";
    public static final String UPDATE = "/update";
    public static final String DELETE_BY_ID = "/delete/{visitId}";
    public static final String EDIT = "/edit";
    public static final String DOCTOR_ID = "/doctor/{doctorId}/all";
    public static final String PATIENT_ID = "/patient/{patientId}/all";

    private final VisitDAO visitDAO;
    private final VisitMapper visitMapper;

    private final AlterVisitDAO alterVisitDAO;

    @GetMapping(PANEL)
    public String showVisitPanel(Model model) {
//        List<VisitEntity> visits = visitDAO.findAll();
        List<AlterVisitDTO> visits = alterVisitDAO.findAllAlter();
        model.addAttribute("visits", visits);
        model.addAttribute("updateVisit", new VisitEntity());
        return "visit/visit-panel";
    }

    @GetMapping(SHOW)
    public String showVisit(@PathVariable("visitId") Integer visitId, Model model) {
        VisitEntity visit = visitDAO.findEntityById(visitId)
                .orElseThrow(() -> new NotFoundException("Could not found visit with id: [%s]".formatted(visitId)));
        model.addAttribute("visit", visit);
        return "visit/visit-show";
    }

    @PostMapping(ADD)
    public String addVisit(
            @RequestParam(value = "doctorId") Integer doctorId,
            @RequestParam(value = "patientId") Integer patientId,
            @RequestParam(value = "dateTime") LocalDateTime dateTime,
            @RequestParam(value = "note") String note,
            @RequestParam(value = "status") String status,
            HttpServletRequest request
    ) {
        VisitEntity newVisit = VisitEntity.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .dateTime(dateTime)
                .note(note)
                .status(status)
                .build();
        visitDAO.saveEntity(newVisit);
        // TODO sprawdzić czy jeżeli będę tworzył nową wizytę to doktor i pacjent będą obecni w widoku
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PutMapping(UPDATE)
    public String updateVisit(
            @ModelAttribute("updateVisit") VisitEntity updateVisit,
            HttpServletRequest request
    ) {
        VisitEntity visitEntity = visitDAO.findById(updateVisit.getVisitId())
                .map(visitMapper::mapFromDtoToEntity)
                .orElseThrow(() -> new NotFoundException(
                        "Could not found visit with id: [%s]".formatted(updateVisit.getVisitId())));
        visitEntity.setVisitId(updateVisit.getVisitId());
        visitEntity.setDoctorId(updateVisit.getDoctorId());
        visitEntity.setPatientId(updateVisit.getPatientId());
        visitEntity.setDateTime(updateVisit.getDateTime());
        visitEntity.setNote(updateVisit.getNote());
        visitEntity.setStatus(updateVisit.getStatus());
        visitDAO.saveEntity(visitEntity);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @DeleteMapping(DELETE_BY_ID)
    public String deleteVisitById(@PathVariable Integer visitId, HttpServletRequest request) {
        VisitEntity visitForDelete = visitDAO.findEntityById(visitId)
                .orElseThrow(() -> new NotFoundException("Could not found visit with id: [%s]".formatted(visitId)));
        visitDAO.delete(visitForDelete);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping(DOCTOR_ID)
    public String showVisitsByDoctor(@PathVariable("doctorId") Integer doctorId, Model model) {
//        List<VisitEntity> visits = visitService.getDoctorVisits(doctorId);
//        DoctorDTO doctor = doctorDAO.findEntityById()(doctorId);
//
//        model.addAttribute("visits", visits);
//        model.addAttribute("doctor", doctor);

        return "doctor-visits";
    }

    @GetMapping(PATIENT_ID)
    public String showVisitsByPatient(@PathVariable("patientId") Integer patientId, Model model) {
//        List<VisitEntity> visits = visitService.getPatientVisits(patientId);

//        model.addAttribute("visits", visits);

        return "patient-visits";
    }


    // #########################################################

    @GetMapping
    public String getAllVisits(Model model) {
        List<VisitEntity> visits = visitDAO.findAll();
        model.addAttribute("visits", visits);
        return "visitList"; // TODO zrobić widok wyświetlający wszystkie wizyt
    }

    @GetMapping("/{visitId}") // VISIT_ID
    public ModelAndView getVisitById(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitDetails");
        Optional<VisitEntity> visit = visitDAO.findEntityById(visitId);
        visit.ifPresent(modelAndView::addObject);
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView showCreateForm() {
        ModelAndView modelAndView = new ModelAndView("visitForm");
        modelAndView.addObject("visit", new VisitEntity());
        return modelAndView;
    }

    @PostMapping
    public String createVisit(@ModelAttribute("visit") VisitEntity visitDTO) {
        VisitEntity createdVisit = visitDAO.save(visitDTO);
        return "redirect:/visits/" + createdVisit.getVisitId();
    }

    @GetMapping("/{visitId}/edit")
    public ModelAndView showEditFormGETXXX(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitForm");
        Optional<VisitEntity> existingVisit = visitDAO.findEntityById(visitId);
        existingVisit.ifPresent(modelAndView::addObject);
        return modelAndView;
    }

    @PutMapping("/{visitId}/edit")
    public ModelAndView showEditForm(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitForm");
        Optional<VisitEntity> existingVisit = visitDAO.findEntityById(visitId);
        existingVisit.ifPresent(modelAndView::addObject);
        return modelAndView;
    }


    @PostMapping("/{visitId}")
    public String updateVisit(@PathVariable("visitId") Integer visitId, @ModelAttribute("visit") VisitEntity updatedVisit) {
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
            return "redirect:/visits/" + savedVisit.getVisitId();
        } else {
            return "redirect:/visits";
        }
    }

    @GetMapping("/{visitId}/delete")
    public ModelAndView showDeleteConfirmationGETXXX(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitDelete");
        Optional<VisitEntity> existingVisit = visitDAO.findEntityById(visitId);
        existingVisit.ifPresent(modelAndView::addObject);
        return modelAndView;
    }

    @DeleteMapping("/{visitId}/delete")
    public ModelAndView showDeleteConfirmation(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitDelete");
        Optional<VisitEntity> existingVisit = visitDAO.findEntityById(visitId);
        existingVisit.ifPresent(modelAndView::addObject);
        return modelAndView;
    }


    @PostMapping("/{visitId}/delete")
    public String deleteVisit(@PathVariable("visitId") Integer visitId) {
        Optional<VisitEntity> existingVisit = visitDAO.findEntityById(visitId);
        existingVisit.ifPresent(visitDAO::delete);
        return "redirect:/visits";
    }
}

