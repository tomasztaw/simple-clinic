package pl.taw.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.AlterVisitDAO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;

import java.time.LocalDateTime;
import java.util.List;

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
    public static final String UPDATE_ID = "/update/{visitId}";
    public static final String DELETE_BY_ID = "/delete/{visitId}";
    public static final String EDIT = "/edit";
    public static final String DOCTOR_ID = "/doctor/{doctorId}/all";
    public static final String PATIENT_ID = "/patient/{patientId}/all";

    private final VisitDAO visitDAO;

    private final VisitService visitService;

    private final DoctorDAO doctorDAO;

    private final PatientDAO patientDAO;



    private final AlterVisitDAO alterVisitDAO;

    @GetMapping(PANEL)
    public String showVisitPanel(Model model) {
        List<VisitDTO> visits = visitDAO.findAll();
        model.addAttribute("visits", visits);
        model.addAttribute("updateVisit", new VisitEntity());
        return "visit/visit-panel";
    }

    @GetMapping(SHOW)
    public String showVisit(@PathVariable("visitId") Integer visitId, Model model) {
        VisitDTO visit = visitDAO.findById(visitId);
        model.addAttribute("visit", visit);
        return "visit/visit-show";
    }

    @GetMapping(DOCTOR_ID)
    public String showVisitsByDoctor(@PathVariable("doctorId") Integer doctorId, Model model) {
        List<VisitDTO> visits = visitService.findAllVisitByDoctor(doctorId);
        DoctorDTO doctor = doctorDAO.findById(doctorId);

        model.addAttribute("visits", visits);
        model.addAttribute("doctor", doctor);

        return "doctor/doctor-visits";
    }

    @GetMapping(PATIENT_ID)
    public String showVisitsByPatient(@PathVariable("patientId") Integer patientId, Model model) {
        List<VisitDTO> visits = visitService.findAllByPatient(patientId);
        PatientDTO patient = patientDAO.findById(patientId);

        model.addAttribute("visits", visits);
        model.addAttribute("patient", patient);

        return "patient/patient-visits";
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
        DoctorEntity doctorEntity = doctorDAO.findEntityById(doctorId);
        PatientEntity patientEntity = patientDAO.findEntityById(patientId);
        VisitEntity newVisit = VisitEntity.builder()
                .doctor(doctorEntity)
                .patient(patientEntity)
                .dateTime(dateTime)
                .note(note)
                .status(status)
                .build();
        visitDAO.save(newVisit);
        // TODO sprawdzić czy jeżeli będę tworzył nową wizytę to doktor i pacjent będą obecni w widoku
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PutMapping(UPDATE_ID)
    public String updateVisitById(
            @PathVariable(value = "visitId") Integer visitId,
            @RequestParam(value = "doctorId") Integer doctorId,
            @RequestParam(value = "patientId") Integer patientId,
            @RequestParam(value = "dateTime") LocalDateTime dateTime,
            @RequestParam(value = "note") String note,
            @RequestParam(value = "status") String status,
            HttpServletRequest request
    ) {
        DoctorEntity doctor = doctorDAO.findEntityById(doctorId);
        PatientEntity patient = patientDAO.findEntityById(patientId);
        VisitEntity visitEntity = visitDAO.findEntityById(visitId);

        visitEntity.setDoctor(doctor);
        visitEntity.setPatient(patient);
        visitEntity.setDateTime(dateTime);
        visitEntity.setNote(note);
        visitEntity.setStatus(status);

        visitDAO.save(visitEntity);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PutMapping(UPDATE)
    public String updateVisit(
            @ModelAttribute("updateVisit") VisitEntity updateVisit,
            HttpServletRequest request
    ) {
        DoctorEntity doctor = doctorDAO.findEntityById(updateVisit.getDoctor().getDoctorId());
        PatientEntity patient = patientDAO.findEntityById(updateVisit.getPatient().getPatientId());
        VisitEntity visitEntity = visitDAO.findEntityById(updateVisit.getVisitId());

        visitEntity.setVisitId(updateVisit.getVisitId());
        visitEntity.setDoctor(doctor);
        visitEntity.setPatient(patient);
        visitEntity.setDateTime(updateVisit.getDateTime());
        visitEntity.setNote(updateVisit.getNote());
        visitEntity.setStatus(updateVisit.getStatus());

        visitDAO.save(visitEntity);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PatchMapping("/{visitId}/update-note")
    public String updateVisitNote(
            @PathVariable("visitId") Integer visitId, @RequestBody String newNote, HttpServletRequest request) {
        VisitEntity visitForUpdate = visitDAO.findEntityById(visitId);
        visitForUpdate.setNote(newNote);
        visitDAO.save(visitForUpdate);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @DeleteMapping(DELETE_BY_ID)
    public String deleteVisitById(@PathVariable Integer visitId, HttpServletRequest request) {
        VisitEntity visitForDelete = visitDAO.findEntityById(visitId);
        visitDAO.delete(visitForDelete);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    // #########################################################

    @GetMapping
    public String getAllVisits(Model model) {
        List<VisitDTO> visits = visitDAO.findAll();
        model.addAttribute("visits", visits);
        return "visitList"; // TODO zrobić widok wyświetlający wszystkie wizyt
    }

    @GetMapping("/{visitId}") // VISIT_ID
    public ModelAndView getVisitById(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitDetails");
        VisitEntity visit = visitDAO.findEntityById(visitId);
        modelAndView.addObject(visit);
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
        VisitEntity createdVisit = visitDAO.saveAndReturn(visitDTO);
        return "redirect:/visits/" + createdVisit.getVisitId();
    }

    @GetMapping("/{visitId}/edit")
    public ModelAndView showEditFormGETXXX(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitForm");
        VisitDTO existingVisit = visitDAO.findById(visitId);
        modelAndView.addObject(existingVisit);
        return modelAndView;
    }

    @PutMapping("/{visitId}/edit")
    public ModelAndView showEditForm(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitForm");
        VisitEntity existingVisit = visitDAO.findEntityById(visitId);
        modelAndView.addObject(existingVisit);
        return modelAndView;
    }


    @PostMapping("/{visitId}")
    public String updateVisit(@PathVariable("visitId") Integer visitId, @ModelAttribute("visit") VisitEntity updatedVisit) {
        VisitEntity existingVisit = visitDAO.findEntityById(visitId);
        DoctorEntity doctor = doctorDAO.findEntityById(updatedVisit.getDoctor().getDoctorId());
        PatientEntity patient = patientDAO.findEntityById(updatedVisit.getPatient().getPatientId());

        VisitEntity updatedVisitWithId = VisitEntity.builder()
                .visitId(existingVisit.getVisitId())
                .doctor(doctor)
                .patient(patient)
                .dateTime(updatedVisit.getDateTime())
                .note(updatedVisit.getNote())
                .status(updatedVisit.getStatus())
                .build();

        VisitEntity savedVisit = visitDAO.saveAndReturn(updatedVisitWithId);
        return "redirect:/visits/" + savedVisit.getVisitId();

    }

    @GetMapping("/{visitId}/delete")
    public ModelAndView showDeleteConfirmationGETXXX(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitDelete");
        VisitEntity existingVisit = visitDAO.findEntityById(visitId);
        modelAndView.addObject(existingVisit);
        return modelAndView;
    }

    @DeleteMapping("/{visitId}/delete")
    public ModelAndView showDeleteConfirmation(@PathVariable("visitId") Integer visitId) {
        ModelAndView modelAndView = new ModelAndView("visitDelete");
        VisitEntity existingVisit = visitDAO.findEntityById(visitId);
        modelAndView.addObject(existingVisit);
        return modelAndView;
    }


    @PostMapping("/{visitId}/delete")
    public String deleteVisit(@PathVariable("visitId") Integer visitId) {
        VisitEntity existingVisit = visitDAO.findEntityById(visitId);
        visitDAO.delete(existingVisit);
        return "redirect:/visits";
    }
}

