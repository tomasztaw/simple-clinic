package pl.taw.api.controller.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.DoctorsDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.api.dto.VisitsDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;

import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping(DoctorRestController.API_DOCTORS)
@AllArgsConstructor
public class DoctorRestController {

    public static final String API_DOCTORS = "/api/doctors";
    public static final String DOCTOR_ID = "/{doctorId}";
    public static final String HISTORY = "/{doctorId}/history";
    public static final String DOCTOR_ID_RESULT = "/%s";
    public static final String DOCTOR_UPDATE_TITLE = "/{doctorId}/title";
    public static final String DOCTOR_UPDATE_PHONE = "/{doctorId}/phone";
    public static final String DOCTOR_UPDATE_EMAIL = "/{doctorId}/email";

    private final DoctorDAO doctorDAO;
    private final VisitService visitService;


    // ################### Ćwiczenie walidacji #####################
    private final GreetingService service;

    @GetMapping("/error")
    public ResponseEntity<?> thrException() {
        return ResponseEntity.ok(service.throwException());
    }

    @PostMapping("walidacja")
    public ResponseEntity<String> postWalidacja(
            @RequestBody Greeting greeting
    ) {
        final String greetingMsg = service.saveGreeting(greeting);
        return ResponseEntity
                .accepted()
                .body(greetingMsg);
    }

    @PostMapping("wal")
    public ResponseEntity<String> doctorValidation(
            @RequestBody DoctorDTO doctor
    ) {
        String body = service.saveDoctor(doctor);
        return ResponseEntity
                .accepted()
                .body(body);
    }

//    @PostMapping("walidacja")
//    public ResponseEntity<String> postWalidacja(
//            @RequestBody @Valid Greeting greeting,
//            BindingResult bindingResult
//    ) {
//        if (bindingResult.hasErrors()) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(
//                            bindingResult.getAllErrors().stream()
//                                    .map(ObjectError::getDefaultMessage)
//                                    .collect(Collectors.joining())
//                    );
//        }
//        final String greetingMsg = service.saveGreeting(greeting);
//        return ResponseEntity
//                .accepted()
//                .body(greetingMsg);
//    }

    // #############################################################

    @GetMapping
    public DoctorsDTO getDoctors() {
        return DoctorsDTO.of(doctorDAO.findAll());
    }

    @GetMapping(value = DOCTOR_ID,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public DoctorDTO showDoctorDetails(@PathVariable("doctorId") Integer doctorId) {
        return doctorDAO.findById(doctorId);
    }

    // TODO czy to czasem nie powinna być w wizytach???
    @GetMapping(value = HISTORY, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<VisitsDTO> showHistory(@PathVariable("doctorId") Integer doctorId) {
        DoctorDTO doctor = doctorDAO.findById(doctorId);
        VisitsDTO visits = VisitsDTO.of(visitService.findAllVisitByDoctor(doctorId));

        if (doctor != null && visits != null) {
            return ResponseEntity.ok(visits);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<DoctorDTO> addDoctor(@Valid @RequestBody DoctorDTO doctorDTO) {
        DoctorEntity doctorEntity = DoctorEntity.builder()
                .name(doctorDTO.getName())
                .surname(doctorDTO.getSurname())
                .title(doctorDTO.getTitle())
                .phone(doctorDTO.getPhone())
                .email(doctorDTO.getEmail())
                .build();
        DoctorEntity created = doctorDAO.saveAndReturn(doctorEntity);

        return ResponseEntity
                .created(URI.create(API_DOCTORS + DOCTOR_ID_RESULT.formatted(created.getDoctorId())))
                .build();
    }

    @PutMapping(DOCTOR_ID)
    @Transactional
    public ResponseEntity<?> updateDoctor(
            @PathVariable("doctorId") Integer doctorId,
            @Valid @RequestBody DoctorDTO doctorDTO
    ) {
        DoctorEntity existingDoctor = doctorDAO.findEntityById(doctorId);

        existingDoctor.setName(doctorDTO.getName());
        existingDoctor.setSurname(doctorDTO.getSurname());
        existingDoctor.setTitle(doctorDTO.getTitle());
        existingDoctor.setPhone(doctorDTO.getPhone());
        existingDoctor.setEmail(doctorDTO.getEmail());

        doctorDAO.save(existingDoctor);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(DOCTOR_ID)
    public ResponseEntity<?> deleteDoctor(
            @PathVariable("doctorId") Integer doctorId
    ) {
        DoctorEntity existingDoctor = doctorDAO.findEntityById(doctorId);
        if (existingDoctor == null) {
            return ResponseEntity.notFound().build();
        }
        doctorDAO.delete(existingDoctor);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(DOCTOR_UPDATE_TITLE)
    public ResponseEntity<?> updateDoctorTitle(
            @PathVariable("doctorId") Integer doctorId,
            @RequestParam(required = true) String newTitle
    ) {
        DoctorEntity existingDoctor = doctorDAO.findEntityById(doctorId);

        existingDoctor.setTitle(newTitle);

        doctorDAO.save(existingDoctor);

        return ResponseEntity.ok().build();
    }

    @PatchMapping(DOCTOR_UPDATE_PHONE)
    public ResponseEntity<?> updateDoctorPhone(
            @PathVariable("doctorId") Integer doctorId,
            @Valid @RequestParam(required = true) String newPhone
    ) {
        DoctorEntity existingDoctor = doctorDAO.findEntityById(doctorId);

        existingDoctor.setPhone(newPhone);

        doctorDAO.save(existingDoctor);

        return ResponseEntity.ok().build();
    }

    @PatchMapping(DOCTOR_UPDATE_EMAIL)
    public ResponseEntity<?> updateDoctorEmail(
            @PathVariable("doctorId") Integer doctorId,
            @Valid @RequestParam(required = true) String newEmail
    ) {
        if (!isValidEmail(newEmail)) {
            return ResponseEntity.badRequest().build();
        }
        DoctorEntity existingDoctor = doctorDAO.findEntityById(doctorId);
        existingDoctor.setEmail(newEmail);
        doctorDAO.save(existingDoctor);

        return ResponseEntity.ok().build();
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";

        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }


}
