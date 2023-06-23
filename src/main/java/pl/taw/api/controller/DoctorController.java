package pl.taw.api.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.business.DoctorService;
import pl.taw.business.WorkingHours;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.repository.jpa.DoctorJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.DoctorMapper;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(DoctorController.DOCTORS)
@AllArgsConstructor
public class DoctorController {

    public static final String DOCTORS = "/doctors";
    public static final String DOCTOR_ID = "/{doctorId}";
    public static final String PANEL = "/panel";
    public static final String SHOW = "/show/{doctorId}";
    public static final String ADD = "/add";
    public static final String UPDATE = "/update";
    public static final String DELETE_ID = "/delete/{doctorId}";
    public static final String SPECIALIZATION = "/specialization/{specialization}";
    public static final String SPECIALIZATIONS = "/specializations";
    public static final String OPINIONS = "/opinions";
    public static final String SCHEDULE = "/schedule/{doctorId}";
    public static final String DOCTOR_ID_JS = "/js/{doctorId}";
    public static final String DOCTOR_ID_XX = "/xx/{doctorId}";
    public static final String DOCTOR_UPDATE_TITLE = "/{doctorId}/title";


    private DoctorDAO doctorDAO;
    private DoctorService doctorService;


    @GetMapping(PANEL)
    public String doctorsPanel(Model model) {
        List<DoctorDTO> doctors = doctorDAO.findAll().stream().toList();
        model.addAttribute("doctors", doctors);
        model.addAttribute("updateDoctor", new DoctorDTO());
        return "doctor/doctor-panel";
    }

    @PostMapping(ADD)
    public String addDoctor(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "surname") String surname,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "phone") String phone,
            @RequestParam(value = "email") String email,
            HttpServletRequest request) {
        DoctorEntity newDoctor = DoctorEntity.builder()
                .name(name)
                .surname(surname)
                .title(title)
                .phone(phone)
                .email(email)
                .build();
        doctorDAO.save(newDoctor);
//        return "redirect:/doctors/doctor-panel";
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PutMapping(UPDATE)
    public String updateDoctor(
            @ModelAttribute("updateDoctor") DoctorDTO updateDoctor,
            HttpServletRequest request) {
        DoctorEntity doctorEntity = doctorDAO.findEntityById(updateDoctor.getDoctorId());
        doctorEntity.setName(updateDoctor.getName());
        doctorEntity.setSurname(updateDoctor.getSurname());
        doctorEntity.setTitle(updateDoctor.getTitle());
        doctorEntity.setPhone(updateDoctor.getPhone());
        doctorEntity.setEmail(updateDoctor.getEmail());
        doctorDAO.save(doctorEntity);
//        return "redirect:/doctors/doctor-panel";
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @DeleteMapping(DELETE_ID)
    public String deleteDoctorById(
            @PathVariable Integer doctorId, HttpServletRequest request) {
        DoctorEntity doctorForDelete = doctorDAO.findEntityById(doctorId);
        if (doctorForDelete != null) {
            doctorDAO.delete(doctorForDelete);
        } else {
            throw new EntityNotFoundException("Could not found doctor with id: [%s]".formatted(doctorId));
        }
        // TODO zmienić na powrót do poprzedniej strony
//        return "redirect:/doctors/panel";
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }


    // DZIAŁA !!!!
    @GetMapping(SPECIALIZATION)
    public String doctorsBySpecializationsView(@PathVariable String specialization, Model model) {
        List<DoctorDTO> doctors = doctorDAO.findBySpecialization(specialization);
        model.addAttribute("doctors", doctors);
        model.addAttribute("specialization", specialization);
        return "doctor/doctors-specialization";
    }

    // DZIAŁA !!!
    @GetMapping(value = SPECIALIZATIONS)
    public String specializations(Model model) {
        List<String> specializations = doctorDAO.findAll().stream()
                .map(DoctorDTO::getTitle)
                .distinct()
                .toList();

        model.addAttribute("specializations", specializations);
        return "doctor/specializations";
    }

    // DZIAŁA !!!
    @GetMapping(SHOW)
    public String showDoctorDetails(
            @PathVariable Integer doctorId, Model model) {
        DoctorDTO doctor = doctorDAO.findById(doctorId);
        model.addAttribute("doctor", doctor);
        List<WorkingHours> workingHours = doctorService.getWorkingHours(doctorId);
        model.addAttribute("workingHours", workingHours);

        return "doctor/doctor-show";
    }

    @GetMapping
    public String doctors(Model model) {
        List<DoctorDTO> doctors = doctorDAO.findAll();
        model.addAttribute("doctors", doctors);
        return "doctor/doctors-logo";
    }

    @GetMapping(value = DOCTOR_ID,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public DoctorDTO doctorDetails(@PathVariable Integer doctorId) {
        return doctorDAO.findById(doctorId);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<DoctorDTO> addDoctor(
            @Valid @RequestBody DoctorDTO doctorDTO
    ) {
        DoctorEntity doctorEntity = DoctorEntity.builder()
                .name(doctorDTO.getName())
                .surname(doctorDTO.getSurname())
                .title(doctorDTO.getTitle())
                .phone(doctorDTO.getPhone())
                .email(doctorDTO.getEmail())
                .build();
        DoctorEntity created = doctorDAO.saveAndReturn(doctorEntity);
        return ResponseEntity
                .created(URI.create(DOCTORS.concat("/%s").formatted(created.getDoctorId())))
                .build();
    }

    @PutMapping(DOCTOR_ID)
    @Transactional
    public ResponseEntity<?> updateDoctor(
            @PathVariable Integer doctorId,
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

    @PatchMapping(DOCTOR_UPDATE_TITLE)
    public ResponseEntity<?> updateTitle(
            @PathVariable Integer doctorId,
            @RequestParam(defaultValue = "doctor") String newTitle
    ) {
        DoctorEntity existingDoctor = doctorDAO.findEntityById(doctorId);
        existingDoctor.setTitle(newTitle);
        doctorDAO.save(existingDoctor);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(DOCTOR_ID)
    public ResponseEntity<?> deleteDoctor(@PathVariable Integer doctorId) {
        DoctorEntity doctorForDelete = doctorDAO.findEntityById(doctorId);
        if (doctorForDelete != null) {
            doctorDAO.delete(doctorForDelete);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // jakieś głupoty - może się wykorzysta

    @GetMapping(value = DOCTOR_ID_XX, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String doctorDetails(@PathVariable Integer doctorId, Model model) {
        DoctorDTO doctor = doctorDAO.findById(doctorId);
        model.addAttribute("doctor", doctor);
        return "doctor-details";
    }

    @GetMapping(value = DOCTOR_ID_JS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DoctorDTO doctorDetailsJS(@PathVariable Integer doctorId) {
        return doctorDAO.findById(doctorId);
    }

    @GetMapping("/doctors/specialization/{specialization}")
    public String doctorsBySpecializationsView_2(@PathVariable("specialization") String specialization, Model model) {
        List<DoctorDTO> doctors = doctorDAO.findAll().stream()
                .filter(doctor -> specialization.equalsIgnoreCase(doctor.getTitle()))
                .collect(Collectors.toList());
        model.addAttribute("doctors", doctors);
        model.addAttribute("specialization", specialization);
        return "doctors-s";
    }

    // grafik lekarza # nie wiem jeszcze czy będzie
    @GetMapping(SCHEDULE)
    public String getDoctorSchedule(@PathVariable Integer doctorId, Model model) {
        DoctorDTO doctor = doctorDAO.findById(doctorId);
        List<WorkingHours> workingHoursList = doctorService.getWorkingHours(doctorId);
        model.addAttribute("workingHoursList", workingHoursList);
        model.addAttribute("doctor", doctor);
        return "doctor-schedule-date";
    }
}
