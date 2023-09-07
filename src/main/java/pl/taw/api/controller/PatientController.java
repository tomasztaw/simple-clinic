package pl.taw.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.PatientsDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.jpa.PatientJpaRepository;
import pl.taw.infrastructure.security.ClinicUserDetailsService;
import pl.taw.infrastructure.security.UserEntity;
import pl.taw.infrastructure.security.UserRepository;

import java.net.URI;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping(PatientController.PATIENTS)
@AllArgsConstructor
public class PatientController {

    public static final String PATIENTS = "/patients";
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
    public static final String SHOW_ALL = "/all";

    private final PatientJpaRepository patientJpaRepository;
    private final PatientDAO patientDAO;
    private final OpinionDAO opinionDAO;
    private final VisitService visitService;
    private final UserRepository userRepository;


    @GetMapping
    public String patientPage(Authentication authentication, Model model) {
        ClinicUserDetailsService clinicUserDetailsService = new ClinicUserDetailsService(userRepository);
        UserDetails details = clinicUserDetailsService.loadUserByUsername(authentication.getName());
        String userEmail = clinicUserDetailsService.getUserEmailAfterAuthentication(authentication.getName());
        UserEntity user = UserEntity.builder()
                .userName(details.getUsername())
                .email(userEmail)
                .build();
        PatientDTO patient = patientDAO.findByEmail(user.getEmail());
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
    public String showHistory(@PathVariable("patientId") Integer patientId,
                              Authentication authentication,
                              Model model
    ) {
        PatientDTO patient = patientDAO.findById(patientId);
        List<VisitDTO> visits = visitService.findAllVisitByPatient(patientId);
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("username", username);
        }
        model.addAttribute("patient", patient);
        model.addAttribute("visits", visits);
        return "patient/patient-history";
    }

    @PatchMapping(PATIENT_UPDATE_PHONE_VIEW) // "/phone-view/{patientId}"
    public String showUpdatePhoneView(
            @PathVariable("patientId") Integer patientId, Model model) {
        PatientDTO patient = patientDAO.findById(patientId);
        model.addAttribute("patient", patient);
        return "core/update-phone";
    }

//    @PatchMapping(PATIENT_UPDATE_PHONE) // "/{patientId}/phone"
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

    @GetMapping(PANEL)
    public String patientsPanel(Model model, Authentication authentication) {
        List<PatientDTO> patients = patientDAO.findAll();
        model.addAttribute("patients", patients);
        model.addAttribute("updatePatient", new PatientDTO());
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("username", username);
        }
        return "patient/patient-panel";
    }

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
        PatientEntity saved = patientDAO.saveAndReturn(createdPatient);
        if ("login".equals(context)) {
            int patientId = saved.getPatientId();
            return "redirect:dashboard/" + patientId;
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<PatientDTO> addRequestPatient(
            @Valid @RequestBody PatientDTO patientDTO
    ) {
        PatientEntity patientEntity = PatientEntity.builder()
                .name(patientDTO.getName())
                .surname(patientDTO.getSurname())
                .pesel(patientDTO.getPesel())
                .phone(patientDTO.getPhone())
                .email(patientDTO.getEmail())
                .build();
        PatientEntity created = patientDAO.saveAndReturn(patientEntity);
        return ResponseEntity
                .created(URI.create(PATIENTS + PATIENT_ID_RESULT.formatted(created.getPatientId())))
                .build();
    }

    @PutMapping(UPDATE)
    public String updatePatient(
            @ModelAttribute("updatePatient") PatientDTO updatePatientDTO,
            HttpServletRequest request
    ) {
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

    @PutMapping(PATIENT_ID)
    @Transactional
    public ResponseEntity<?> updateRequestPatient(
            @PathVariable Integer patientId,
            @Valid @RequestBody PatientDTO patientDTO
    ) {
        PatientEntity existingPatient = patientDAO.findEntityById(patientId);

        existingPatient.setName(patientDTO.getName());
        existingPatient.setSurname(patientDTO.getSurname());
        existingPatient.setPesel(patientDTO.getPesel());
        existingPatient.setPhone(patientDTO.getPhone());
        existingPatient.setEmail(patientDTO.getEmail());

        patientDAO.save(existingPatient);

        return ResponseEntity.ok().build();
    }

    @GetMapping(SHOW)
    public String showPatient(
            @PathVariable Integer patientId,
            Model model
    ) {
        PatientDTO patient = patientDAO.findById(patientId);
        List<VisitDTO> visits = visitService.findAllVisitByPatient(patientId);
        List<OpinionDTO> opinions = opinionDAO.findAllByPatient(patientId);
        model.addAttribute("patient", patient);
        model.addAttribute("visits", visits);
        model.addAttribute("opinions", opinions);

        return "patient/patient-show";
    }

    @GetMapping(value = PATIENT_ID, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody // jeżeli chcę dostać odpowiedź w terminalu!!!
    public PatientDTO patientDetails(@PathVariable Integer patientId) {
        return patientDAO.findById(patientId);
    }

    @GetMapping(value = SHOW_ALL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PatientDTO> patientsList() {
        return patientDAO.findAll();
    }

    @GetMapping(value = SHOW_ALL + "/dots", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PatientsDTO patientsDTOList() {
        return PatientsDTO.of(patientDAO.findAll());
    }

    @DeleteMapping(DELETE)
    public ResponseEntity<String> deletePatientById(
            @PathVariable Integer patientId,
            HttpServletRequest request
    ) {
        PatientEntity existingPatient = patientDAO.findEntityById(patientId);
        if (existingPatient != null) {
            patientDAO.delete(existingPatient);
            String referer = request.getHeader("Referer");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", referer);
            return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(PATIENT_ID)
    public ResponseEntity<?> deletePatient(
            @PathVariable Integer patientId
    ) {
        PatientEntity existingPatient = patientDAO.findEntityById(patientId);
        if (existingPatient != null) {
            patientDAO.delete(existingPatient);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // można w przyszłości pomyśleć nad aktualizacją tylko części danych (telefon, email)
    // w panelu aktualizacji wyświetlają się aktualne dane i poprawia się tylko potrzebne, reszta zostaje

    // próba nowej aktualizacji telefonu - nie działa
    @GetMapping("/telefon")
    public String aktualizujTelefon(Authentication authentication, Model model) {
        ClinicUserDetailsService clinicUserDetailsService = new ClinicUserDetailsService(userRepository);
        UserDetails details = clinicUserDetailsService.loadUserByUsername(authentication.getName());
        String userEmail = clinicUserDetailsService.getUserEmailAfterAuthentication(authentication.getName());
        UserEntity user = UserEntity.builder()
                .userName(details.getUsername())
                .email(userEmail)
                .build();
        PatientDTO patient = patientDAO.findByEmail(user.getEmail());
        model.addAttribute("patient", patient);

        return "patient/patient-phone";
    }

    @PatchMapping("/updatePhoneNumber")
    public String updatePhoneNumber(@ModelAttribute("patient") PatientDTO patient) {
        PatientEntity patientForUpdatePhone = patientDAO.findEntityById(patient.getPatientId());
        patientForUpdatePhone.setPhone(patient.getPhone());
        patientJpaRepository.save(patientForUpdatePhone);

//        return "redirect:/patients/dashboard/" + patient.getPatientId();
        // po odświeżeniu strony zmienia się ścieżka do styli i nie są one ładowane
        return "redirect:/";
    }

    @GetMapping("/email")
    public String aktualizujEmail(Authentication authentication, Model model) {
        ClinicUserDetailsService clinicUserDetailsService = new ClinicUserDetailsService(userRepository);
        UserDetails details = clinicUserDetailsService.loadUserByUsername(authentication.getName());
        String userEmail = clinicUserDetailsService.getUserEmailAfterAuthentication(authentication.getName());
        UserEntity user = UserEntity.builder()
                .userName(details.getUsername())
                .email(userEmail)
                .build();
        PatientDTO patient = patientDAO.findByEmail(user.getEmail());
        model.addAttribute("patient", patient);

        return "patient/patient-email";
    }

    @PatchMapping("/updateEmail")
    public String updateEmail(@ModelAttribute("patient") PatientDTO patient) {
        PatientEntity patientForUpdatePhone = patientDAO.findEntityById(patient.getPatientId());
        patientForUpdatePhone.setEmail(patient.getEmail());
        // TODO a co z kredkami?!
        patientJpaRepository.save(patientForUpdatePhone);

//        return "redirect:/patients/dashboard/" + patient.getPatientId();
        // po odświeżeniu strony zmienia się ścieżka do styli i nie są one ładowane
        return "redirect:/";
    }


    // #####################################        RZECZY DO SKASOWANIA    #########################

    public static final String LOGOWANIE = "/logowanie";
    public static final String REGISTER = "/register";


    @GetMapping(LOGOWANIE)
    public String showLoginForm() {
        return "login2";
    }


    @PostMapping(LOGOWANIE)
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
        return "core/register";
    }

    // wyświetlanie na sztywno pacjenta o id: 5
    @GetMapping(DASHBOARD)
    public String showDashboard(Model model) {
        String pesel = "85061718378";
        PatientDTO patient = patientDAO.findByPesel(pesel);

        model.addAttribute("patient", patient);

        return "patient/patient-dashboard";
    }
}
