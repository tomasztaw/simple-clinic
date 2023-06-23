package pl.taw.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.jpa.PatientJpaRepository;

import java.util.List;
import java.util.Random;

@Controller
@RequestMapping(PatientController.PATIENTS)
@AllArgsConstructor
public class PatientController {

    public static final String PATIENTS = "/patients";
    public static final String LOGIN = "/login";
    public static final String DASHBOARD = "/dashboard";
    public static final String DASHBOARD_ID = "/dashboard/{patientId}";
    public static final String PATIENT_ID = "/{patientId}";
    public static final String PATIENT_UPDATE_PHONE = "/{patientId}/phone";
    public static final String PATIENT_UPDATE_PHONE_VIEW = "/phone-view/{patientId}";
    public static final String PATIENT_ID_RESULT = "/%s";
    public static final String HISTORY = "/history/{patientId}";
    public static final String PANEL = "/panel";
    public static final String ADD = "/add";
    public static final String UPDATE = "/update";
    public static final String SHOW = "/show/{patientId}";
    public static final String DELETE = "/delete/{patientId}";
    public static final String REGISTER = "/register";

    private final PatientJpaRepository patientJpaRepository;
    private final PatientDAO patientDAO;
    // dodanie wizyt
    private final VisitService visitService;

    @GetMapping(LOGIN)
    public String showLoginForm() {
        return "login2";
    }


    @PostMapping(LOGIN)
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        if (username.equals("user") && password.equals("test")) {
            // losowanie pacjent
            Random random = new Random();
            int size = patientDAO.findAll().size();
            int patientId = random.nextInt(size) + 1;

            return "redirect:/patients/dashboard/" + patientId;
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }

    @GetMapping(REGISTER)
    public String registerPatient() {
        return "register";
    }

    // wyświetlanie na sztywno pacjenta o id: 5
    @GetMapping(DASHBOARD)
    public String showDashboard(Model model) {
        String pesel = "85061718378";
        PatientDTO patient = patientDAO.findByPesel(pesel);

        model.addAttribute("patient", patient);

        return "patient/patient-dashboard";
    }

    @GetMapping(DASHBOARD_ID)
    public String showDashboardWithId(
            @PathVariable("patientId") Integer patientId, Model model) {
        PatientDTO patient = patientDAO.findById(patientId);

        model.addAttribute("patient", patient);

        return "patient/patient-dashboard";
    }

    @GetMapping(HISTORY)
    public String showHistory(@PathVariable("patientId") Integer patientId, Model model) {
        PatientDTO patient = patientDAO.findById(patientId);
        List<VisitDTO> visits = visitService.findAllVisitByPatient(patientId);

        model.addAttribute("patient", patient);
        model.addAttribute("visits", visits);
        return "patient/patient-visits";
    }

    @PatchMapping(PATIENT_UPDATE_PHONE_VIEW) // "/phone-view/{patientId}"
    public String showUpdatePhoneView(
            @PathVariable("patientId") Integer patientId, Model model) {
        PatientDTO patient = patientDAO.findById(patientId);
        model.addAttribute("patient", patient);
        return "update-phone";
    }

    @PostMapping(PATIENT_UPDATE_PHONE) // "/{patientId}/phone"
    public String updatePatientPhoneView(
            @PathVariable("patientId") Integer patientId,
            @RequestParam(required = true, value = "newPhone") String newPhone,
            @RequestParam(required = false, value = "referer") String referer) {
        PatientEntity patient = patientDAO.findEntityById(patientId);
        patient.setPhone(newPhone);
        patientDAO.save(patient);

        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        } else {
            return "redirect:/patients/dashboard/" + patientId;
        }
    }


    // ok
    @GetMapping(PANEL)
    public String patientsPanel(Model model) {
        List<PatientDTO> patients = patientDAO.findAll();
        model.addAttribute("patients", patients);
        model.addAttribute("updatePatient", new PatientDTO());
        return "patient/patient-panel";
    }

    // ok
    @PostMapping(ADD)
    public String addPatient(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "surname") String surname,
            @RequestParam(value = "pesel") String pesel,
            @RequestParam(value = "phone") String phone,
            @RequestParam(value = "email") String email,
            @RequestParam("context") String context,
            HttpServletRequest request) {
        PatientEntity createdPatient = PatientEntity.builder()
                .name(name)
                .surname(surname)
                .pesel(pesel)
                .phone(phone)
                .email(email)
                .build();
        patientDAO.save(createdPatient);
        if ("login".equals(context)) {
            int patientId = patientDAO.findByPesel(pesel).getPatientId();
            return "redirect:dashboard/" + patientId;
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    // ok
    @PutMapping(UPDATE)
    public String updatePatient(
            @ModelAttribute("updatePatient") PatientDTO updatePatientDTO,
            HttpServletRequest request) {
        PatientEntity existingPatient = patientDAO.findEntityById(updatePatientDTO.getPatientId());
        existingPatient.setName(updatePatientDTO.getName());
        existingPatient.setSurname(updatePatientDTO.getSurname());
        existingPatient.setPesel(updatePatientDTO.getPesel());
        existingPatient.setPhone(updatePatientDTO.getPhone());
        existingPatient.setEmail(updatePatientDTO.getEmail());
        patientDAO.save(existingPatient);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    // ok
    @GetMapping(SHOW)
    public String showPatient(
            @PathVariable Integer patientId,
            Model model
    ) {
        PatientDTO patient = patientDAO.findById(patientId);
        List<VisitDTO> visits = visitService.findAllVisitByPatient(patientId);
//        List<OpinionDTO> opinions = opinionDAO.findByPatientId(patientId);
//        List<PrescriptionDTO> prescriptions = prescriptionDAO.findByPatientId(patientId);
        model.addAttribute("patient", patient);
        model.addAttribute("visits", visits);
//        model.addAttribute("opinions", opinions);
//        model.addAttribute("prescriptions", prescriptions);
        return "patient/patient-show";
    }

    // ok
    @DeleteMapping(DELETE)
    public String deletePatientById(
            @PathVariable Integer patientId,
            HttpServletRequest request
    ) {
        patientJpaRepository.deleteById(patientId);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    // można w przyszłości pomyśleć nad aktualizacją tylko części danych (telefon, email)
    // w panelu aktualizacji wyświetlają się aktualne dane i poprawia się tylko potrzebne, reszta zostaje
}
