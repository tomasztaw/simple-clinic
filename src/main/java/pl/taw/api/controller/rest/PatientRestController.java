package pl.taw.api.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.jpa.PatientJpaRepository;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(PatientRestController.API_PATIENTS)
@AllArgsConstructor
public class PatientRestController {

    public static final String API_PATIENTS = "/api/patients";
    public static final String LOGIN = "/login";
    public static final String PATIENT_ID = "/{patientId}";
    public static final String DASHBOARD_ID = "/dashboard/{patientId}";
    public static final String HISTORY = "/history/{patientId}";
    public static final String PATIENT_UPDATE_PHONE = "/{patientId}/phone";
    public static final String ADD = "/add";
    public static final String UPDATE_ID = "/{patientId}/update";
    public static final String DELETE_ID = "/{patientId}/delete";


    private final PatientDAO patientDAO;
    private final VisitService visitService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientDAO.findAll();
        if (patients.size() > 0) {
            return ResponseEntity.ok(patients);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(LOGIN)
    public ResponseEntity<String> showLoginForm() {
        return ResponseEntity.ok("login2");
    }

    @PostMapping(LOGIN)
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        if (username.equals("user") && password.equals("test")) {
            // Losowanie pacjenta
            Random random = new Random();
            int size = patientDAO.findAll().size();
            int patientId = random.nextInt(size) + 1;

            return ResponseEntity.ok("/patients/dashboard/" + patientId);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    @GetMapping(DASHBOARD_ID)
    public ResponseEntity<PatientDTO> showDashboardWithId(@PathVariable("patientId") Integer patientId) {
        PatientDTO patient = patientDAO.findById(patientId);

        if (patient != null) {
            return ResponseEntity.ok(patient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(HISTORY)
    public ResponseEntity<List<VisitDTO>> showHistory(@PathVariable("patientId") Integer patientId) {
        PatientDTO patient = patientDAO.findById(patientId);
        List<VisitDTO> visits = visitService.findAllVisitByPatient(patientId);

        if (patient != null && visits != null) {
            return ResponseEntity.ok(visits);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = PATIENT_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientDTO> showPatientById(@PathVariable("patientId") Integer patientId) {
        PatientDTO patient = patientDAO.findById(patientId);

        if (patient != null) {
            return ResponseEntity.ok(patient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(PATIENT_UPDATE_PHONE)
    public ResponseEntity<String> updatePatientPhone(
            @PathVariable("patientId") Integer patientId,
            @RequestParam("newPhone") String newPhone) {
        PatientEntity patient = patientDAO.findEntityById(patientId);

        if (patient != null) {
            patient.setPhone(newPhone);
            patientDAO.save(patient);
            String answer = String.format("Numer zaktualizowany na [%s] dla pacjenta [%s %s]"
                    .formatted(newPhone, patient.getName(), patient.getSurname()));
            return ResponseEntity.ok(answer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(ADD)
    public ResponseEntity<String> addPatient(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "surname") String surname,
            @RequestParam(value = "pesel") String pesel,
            @RequestParam(value = "phone") String phone,
            @RequestParam(value = "email") String email) {
        PatientEntity createdPatient = PatientEntity.builder()
                .name(name)
                .surname(surname)
                .pesel(pesel)
                .phone(phone)
                .email(email)
                .build();
        patientDAO.save(createdPatient);

        return ResponseEntity.ok("Dodano pacjenta: " + name + " " + surname);
    }

    @PutMapping(UPDATE_ID)
    public ResponseEntity<String> updatePatient(
            @PathVariable("patientId") Integer patientId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "surname") String surname,
            @RequestParam(value = "pesel") String pesel,
            @RequestParam(value = "phone") String phone,
            @RequestParam(value = "email") String email) {
        PatientEntity existingPatient = patientDAO.findEntityById(patientId);

        if (existingPatient != null) {
            existingPatient.setName(name);
            existingPatient.setSurname(surname);
            existingPatient.setPesel(pesel);
            existingPatient.setPhone(phone);
            existingPatient.setEmail(email);
            patientDAO.save(existingPatient);

            return ResponseEntity.ok("Aktualizacja wykonana");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(DELETE_ID)
    public ResponseEntity<String> deletePatientById(@PathVariable("patientId") Integer patientId) {
        PatientEntity patientForDelete = patientDAO.findEntityById(patientId);

        if (patientForDelete != null) {
            patientDAO.delete(patientForDelete);
            return ResponseEntity.ok("Pacjent wykasowany");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
