package pl.taw.api.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;

import java.util.List;

@RestController
@RequestMapping(DoctorRestController.API_DOCTORS)
@AllArgsConstructor
public class DoctorRestController {

    public static final String API_DOCTORS = "/api/doctors";
    public static final String LOGIN = "/login";
    public static final String DOCTOR_ID = "/{doctorId}";
    public static final String DASHBOARD_ID = "/dashboard/{doctorId}";
    public static final String HISTORY = "/history/{doctorId}";
    public static final String DOCTOR_UPDATE_PHONE = "/{doctorId}/phone";
    public static final String ADD = "/add";
    public static final String UPDATE_ID = "/{doctorId}/update";
    public static final String DELETE_ID = "/{doctorId}/delete";

    private final DoctorDAO doctorDAO;
    private final VisitService visitService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DoctorDTO>> getAllPatients() {
        List<DoctorDTO> doctors = doctorDAO.findAll();
        if (doctors.size() > 0) {
            return ResponseEntity.ok(doctors);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(HISTORY)
    public ResponseEntity<List<VisitDTO>> showHistory(@PathVariable("doctorId") Integer doctorId) {
        DoctorDTO doctor = doctorDAO.findById(doctorId);
        List<VisitDTO> visits = visitService.findAllVisitByDoctor(doctorId);

        if (doctor != null && visits != null) {
            return ResponseEntity.ok(visits);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = DOCTOR_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DoctorDTO> showDoctorById(@PathVariable("doctorId") Integer doctorId) {
        DoctorDTO doctor = doctorDAO.findById(doctorId);

        if (doctor != null) {
            return ResponseEntity.ok(doctor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @PostMapping(DOCTOR_UPDATE_PHONE)
    public ResponseEntity<String> updateDoctorPhone(
            @PathVariable("doctorId") Integer doctorId,
            @RequestParam("newPhone") String newPhone) {
        DoctorEntity doctor = doctorDAO.findEntityById(doctorId);

        if (doctor != null) {
            doctor.setPhone(newPhone);
            doctorDAO.save(doctor);
            String answer = String.format("Numer zaktualizowany na [%s] dla doktora [%s %s]"
                    .formatted(newPhone, doctor.getName(), doctor.getSurname()));
            return ResponseEntity.ok(answer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(ADD)
    public ResponseEntity<String> addDoctor(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "surname") String surname,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "phone") String phone,
            @RequestParam(value = "email") String email) {
        DoctorEntity createdDoctor = DoctorEntity.builder()
                .name(name)
                .surname(surname)
                .title(title)
                .phone(phone)
                .email(email)
                .build();
        doctorDAO.save(createdDoctor);

        return ResponseEntity.ok("Dodano lekarza: " + name + " " + surname);
    }

    @PutMapping(UPDATE_ID)
    public ResponseEntity<String> updateDoctor(
            @PathVariable("doctorId") Integer doctorId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "surname") String surname,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "phone") String phone,
            @RequestParam(value = "email") String email) {
        DoctorEntity existingDoctor = doctorDAO.findEntityById(doctorId);

        if (existingDoctor != null) {
            existingDoctor.setName(name);
            existingDoctor.setSurname(surname);
            existingDoctor.setTitle(title);
            existingDoctor.setPhone(phone);
            existingDoctor.setEmail(email);
            doctorDAO.save(existingDoctor);

            return ResponseEntity.ok("Aktualizacja wykonana");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(DELETE_ID)
    public ResponseEntity<String> deleteDoctorById(@PathVariable("doctorId") Integer doctorId) {
        DoctorEntity doctorForDelete = doctorDAO.findEntityById(doctorId);

        if (doctorForDelete != null) {
            doctorDAO.delete(doctorForDelete);
            return ResponseEntity.ok("Lekarz wykasowany");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
