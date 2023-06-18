package pl.taw.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.VisitDAO;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(VisitController.VISITS)
@AllArgsConstructor
public class VisitController {

    public static final String VISITS = "/visits";
    public static final String PANEL = "/panel";
    public static final String VISIT_ID = "/{visitId}";
    public static final String ADD = "/add";
    public static final String EDIT = "/edit";

    private final VisitDAO visitDAO;

    @GetMapping(PANEL)
    public String showVisitPanel(Model model) {
        List<VisitDTO> visits = visitDAO.findAll();
        model.addAttribute("visits", visits);
        model.addAttribute("updateVisit", new VisitDTO());
        return "visit/visit-panel";
    }

    @GetMapping
    public String getAllVisits(Model model) {
        List<VisitDTO> visits = visitDAO.findAll();
        model.addAttribute("visits", visits);
        return "visitList";
    }

    @GetMapping("/{visitId}") // VISIT_ID
    public ModelAndView getVisitById(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitDetails");
        Optional<VisitDTO> visit = visitDAO.findById(visitId);
        visit.ifPresent(modelAndView::addObject);
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView showCreateForm() {
        ModelAndView modelAndView = new ModelAndView("visitForm");
        modelAndView.addObject("visit", new VisitDTO());
        return modelAndView;
    }

    @PostMapping
    public String createVisit(@ModelAttribute("visit") VisitDTO visitDTO) {
        VisitDTO createdVisit = visitDAO.save(visitDTO);
        return "redirect:/visits/" + createdVisit.getVisitId();
    }

    @GetMapping("/{visitId}/edit")
    public ModelAndView showEditFormGETXXX(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitForm");
        Optional<VisitDTO> existingVisit = visitDAO.findById(visitId);
        existingVisit.ifPresent(modelAndView::addObject);
        return modelAndView;
    }

    @PutMapping("/{visitId}/edit")
    public ModelAndView showEditForm(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitForm");
        Optional<VisitDTO> existingVisit = visitDAO.findById(visitId);
        existingVisit.ifPresent(modelAndView::addObject);
        return modelAndView;
    }


    @PostMapping("/{visitId}")
    public String updateVisit(@PathVariable("visitId") Integer visitId, @ModelAttribute("visit") VisitDTO updatedVisit) {
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
            return "redirect:/visits/" + savedVisit.getVisitId();
        } else {
            return "redirect:/visits";
        }
    }

    @GetMapping("/{visitId}/delete")
    public ModelAndView showDeleteConfirmationGETXXX(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitDelete");
        Optional<VisitDTO> existingVisit = visitDAO.findById(visitId);
        existingVisit.ifPresent(modelAndView::addObject);
        return modelAndView;
    }

    @DeleteMapping("/{visitId}/delete")
    public ModelAndView showDeleteConfirmation(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitDelete");
        Optional<VisitDTO> existingVisit = visitDAO.findById(visitId);
        existingVisit.ifPresent(modelAndView::addObject);
        return modelAndView;
    }


    @PostMapping("/{visitId}/delete")
    public String deleteVisit(@PathVariable("visitId") Integer visitId) {
        Optional<VisitDTO> existingVisit = visitDAO.findById(visitId);
        if (existingVisit.isPresent()) {
            visitDAO.delete(existingVisit.get());
        }
        return "redirect:/visits";
    }
}

