package pl.taw.api.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.*;
import pl.taw.business.DoctorService;
import pl.taw.business.PatientService;
import pl.taw.business.ReservationService;
import pl.taw.business.WorkingHours;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.security.ClinicUserDetailsService;
import pl.taw.infrastructure.security.UserEntity;
import pl.taw.infrastructure.security.UserRepository;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping(DoctorController.DOCTORS)
@AllArgsConstructor
public class DoctorController {

    public static final String DOCTORS = "/doctors";
    public static final String DOCTOR_ID = "/{doctorId}";
    public static final String PANEL = "/panel";
    public static final String SHOW = "/{doctorId}/show";
    public static final String ADD = "/add";
    public static final String VALID = "/valid";
    public static final String UPDATE = "/update";
    public static final String DELETE_ID = "/{doctorId}/delete";
    public static final String SPECIALIZATION = "/specialization/{specialization}";
    public static final String SPECIALIZATIONS = "/specializations";
    public static final String SCHEDULE = "/{doctorId}/schedule";
    public static final String DOCTOR_UPDATE_TITLE = "/{doctorId}/title";
    public static final String PAGINATION = "/pagination";
    public static final String DASHBOARD = "/dashboard";


    private final DoctorDAO doctorDAO;
    private final DoctorService doctorService;
    private final ReservationService reservationService;
    private final PatientDAO patientDAO;
    private final PatientService patientService;
    private final UserRepository userRepository;
    private final OpinionDAO opinionDAO;
    private final VisitDAO visitDAO;
    private final ResourceLoader resourceLoader;

    // TODO do rozbudowanie i przetestowania
    @GetMapping(DASHBOARD)
    public String showDoctorDashboard() {
        return "doctor/doctor-dashboard";
    }

    @GetMapping(DASHBOARD + "-new")
    public String showDoctorDashboardNew(Model model, Authentication authentication) {
        ClinicUserDetailsService clinicUserDetailsService = new ClinicUserDetailsService(userRepository);
        String userEmail = clinicUserDetailsService.getUserEmailAfterAuthentication(authentication.getName());
        DoctorDTO doctor = doctorDAO.findByEmail(userEmail);
        model.addAttribute("doctor", doctor);

        List<ReservationDTO> reservations = reservationService.findAllReservationsByDoctor(doctor.getDoctorId());
        model.addAttribute("reservations", reservations);

        Map<Integer, String> patientsNames = patientDAO.getPatientsFullNamesByIdAll();
        model.addAttribute("names", patientsNames);

        List<OpinionDTO> opinions = opinionDAO.findAllByDoctor(doctor.getDoctorId());
        model.addAttribute("opinions", opinions);

        List<VisitDTO> visits = visitDAO.findAllByDoctor(doctor.getDoctorId());
        model.addAttribute("visits", visits);

        List<PatientDTO> patients = patientDAO.findAll();
        model.addAttribute("patients", patients);

        model.addAttribute("patientService", patientService);

        return "doctor/doctor-dashboard-new";
    }

    @GetMapping
    public String doctors(Model model, Authentication authentication) {
        List<DoctorDTO> doctors = doctorDAO.findAll();
        model.addAttribute("doctors", doctors);
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("username", username);
        }
        return "doctor/doctors-all";
    }

    @GetMapping(SPECIALIZATIONS)
    public String specializations(Model model, Authentication authentication) {
        List<String> specializations = doctorDAO.findAll().stream()
                .map(DoctorDTO::getTitle)
                .distinct()
                .toList();
        model.addAttribute("specializations", specializations);
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("username", username);
        }
        return "doctor/specializations";
    }

    @GetMapping(SPECIALIZATION)
    public String doctorsBySpecializationsView(
            @PathVariable String specialization,
            Model model,
            Authentication authentication
    ) {
        List<DoctorDTO> doctors = doctorDAO.findBySpecialization(specialization);
        model.addAttribute("doctors", doctors);
        model.addAttribute("specialization", specialization);
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("username", username);
        }
        return "doctor/doctors-specialization";
    }

    @GetMapping(PAGINATION)
    public String doctorsPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            Authentication authentication,
            Model model
    ) {
        int adjustedPage = page - 1;
        Page<DoctorDTO> doctorsPage = doctorService.getDoctorsPage(adjustedPage, size);

        model.addAttribute("doctors", doctorsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", doctorsPage.getTotalPages());
        model.addAttribute("selectedSize", size);
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("username", username);
        }

        return "doctor/doctors-pagination";
    }

    // ##################

    @GetMapping("/all-old")
    public String doctorsOld(Model model) {
        List<DoctorDTO> doctors = doctorDAO.findAll();
        model.addAttribute("doctors", doctors);
        return "doctor/doctors-all-old";
    }

    @GetMapping(value = DOCTOR_ID, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public DoctorDTO doctorDetails(@PathVariable Integer doctorId) {
        return doctorDAO.findById(doctorId);
    }

    @GetMapping(SPECIALIZATION + "-old")
    public String doctorsBySpecializationsViewOld(@PathVariable String specialization, Model model) {
        List<DoctorDTO> doctors = doctorDAO.findBySpecialization(specialization);
        model.addAttribute("doctors", doctors);
        model.addAttribute("specialization", specialization);
        return "doctor/doctors-specialization-old";
    }

    @GetMapping(SPECIALIZATIONS + "-old")
    public String specializationsOld(Model model) {
        List<String> specializations = doctorDAO.findAll().stream()
                .map(DoctorDTO::getTitle)
                .distinct()
                .toList();
        model.addAttribute("specializations", specializations);
        return "doctor/specializations-old";
    }

    @GetMapping(SHOW)
    public String showDoctorDetails(
            @PathVariable Integer doctorId,
            Model model,
            Authentication authentication
    ) {
        DoctorDTO doctor = doctorDAO.findById(doctorId);
        model.addAttribute("doctor", doctor);
        List<WorkingHours> workingHours = doctorService.getWorkingHours(doctorId);
        model.addAttribute("workingHours", workingHours);

        String doctorDescFile = "classpath:/desc/doctorDesc" + doctorId + ".txt";
        String defaultDescription = "classpath:/desc/default.txt";

        try {
            Resource doctorResource = resourceLoader.getResource(doctorDescFile);
            Resource defaultResource = resourceLoader.getResource(defaultDescription);
            String description;
            if (doctorResource.exists()) {
                description = new String(doctorResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } else {
                description = new String(defaultResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            }
            model.addAttribute("description", description);
        } catch (IOException e) {
            throw new RuntimeException("Błąd odczytu pliku");
        }

        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("username", username);
        }
        List<OpinionDTO> opinions = opinionDAO.findAllByDoctor(doctorId);
        model.addAttribute("opinions", opinions);

        return "doctor/doctor-show";
    }


    @GetMapping(PANEL)
    public String doctorsPanel(Model model) {
        List<DoctorDTO> doctors = doctorDAO.findAll();
        model.addAttribute("doctors", doctors);
        model.addAttribute("updateDoctor", new DoctorDTO());

        // !!! pamiętaj (autoryzacja)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        model.addAttribute("username", username);

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

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
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

    // TODO do zastanowienia, czy nie powinna być tylko jedna metoda z walidacją
    @PostMapping(ADD + VALID)
    public String addValidDoctor(
            @Valid @ModelAttribute("updateDoctor") DoctorDTO doctorDTO,
            BindingResult bindingResult,
            HttpServletRequest request
    ) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        DoctorEntity newDoctor = DoctorEntity.builder()
                .name(doctorDTO.getName())
                .surname(doctorDTO.getSurname())
                .title(doctorDTO.getTitle())
                .phone(doctorDTO.getPhone())
                .email(doctorDTO.getEmail())
                .build();
        doctorDAO.save(newDoctor);

        String referer = request.getHeader("Referer");
        if (referer != null) {
            return "redirect:" + referer;
        } else {
            return "home";
        }
    }

    // TODO może lepiej zrobić jedną metodę
    @PutMapping(UPDATE)
    public String updateDoctor(
            @Valid @ModelAttribute("updateDoctor") DoctorDTO updateDoctor,
            HttpServletRequest request
    ) {
        DoctorEntity doctorEntity = doctorDAO.findEntityById(updateDoctor.getDoctorId());
        doctorEntity.setName(updateDoctor.getName());
        doctorEntity.setSurname(updateDoctor.getSurname());
        doctorEntity.setTitle(updateDoctor.getTitle());
        doctorEntity.setPhone(updateDoctor.getPhone());
        doctorEntity.setEmail(updateDoctor.getEmail());
        doctorDAO.save(doctorEntity);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
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

    // TODO za dużo tego narobiłem :), druga obsługuje ResponseEntity
    @DeleteMapping(DELETE_ID)
    public String deleteDoctorById(
            @PathVariable Integer doctorId, HttpServletRequest request) {
        DoctorEntity doctorForDelete = doctorDAO.findEntityById(doctorId);
        if (doctorForDelete != null) {
            doctorDAO.delete(doctorForDelete);
        } else {
            throw new EntityNotFoundException("Could not found doctor with id: [%s]".formatted(doctorId));
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
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

    // TODO przydałoby się to uprościć
    @GetMapping(SCHEDULE)
    public String getDoctorScheduleSimpleMap(@PathVariable Integer doctorId, Model model, Authentication authentication) {
        LocalDate today = LocalDate.now();
        DoctorDTO doctor = doctorDAO.findById(doctorId);
        List<WorkingHours> workingHoursList = doctorService.getWorkingHours(doctorId);
        Map<Map<String, LocalDate>, List<WorkingHours>> resultMap = reservationService.getWorkingHoursForCurrentWeek(workingHoursList);
        // Może trzeba będzie pomyśleć nad metodą, która zwraca rezerwacje dla danego tygodnia
        List<ReservationDTO> notAvailableTimes = reservationService.findAllReservationsByDoctor(doctorId);
        List<LocalDateTime> listOfRes = notAvailableTimes.stream().map(item -> LocalDateTime.of(item.getDay(), item.getStartTimeR())).toList();
        Map<String, WorkingHours> simpleMapBeforeCheck;
        if (today.getDayOfWeek().getValue() < 5) {
            simpleMapBeforeCheck = reservationService.simpleMap(workingHoursList);
        } else {
            simpleMapBeforeCheck = reservationService.simpleMapForNextWeek(workingHoursList);
        }
        Map<String, WorkingHours> simpleMap = new LinkedHashMap<>();
        for (Map.Entry<String, WorkingHours> entry : simpleMapBeforeCheck.entrySet()) {
            String date = entry.getKey().substring(entry.getKey().indexOf(" ") + 1);
            WorkingHours workingHours = entry.getValue();
            WorkingHours availableDates = new WorkingHours();
            List<String> times = workingHours.getAppointmentTimes();
            List<String> availableTimes = new ArrayList<>();
            for (String time : times) {
                LocalDateTime forCheck = LocalDateTime.parse(date + " " + time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                if (!listOfRes.contains(forCheck)) {
                    availableTimes.add(time);
                }
            }
            availableDates.setAppointmentTimes(availableTimes);
            if (!availableTimes.isEmpty()) {
                simpleMap.put(entry.getKey(), availableDates);
            }
            availableDates.setStartTime(entry.getValue().getStartTime());
            availableDates.setEndTime(entry.getValue().getEndTime());
        }
        model.addAttribute("doctor", doctor);
        model.addAttribute("simpleMap", simpleMap);
        UserEntity user = userRepository.findByUserName(authentication.getName());
        PatientDTO patient = patientDAO.findByEmail(user.getEmail());
        model.addAttribute("patientId", patient.getPatientId());
        String username = authentication.getName();
        model.addAttribute("username", username);

        return "doctor/doctor-schedule";
    }

    // nowe wyświetlanie widoku
    @GetMapping(SCHEDULE + "-new")
    public String getDoctorsSchedulesNew(@PathVariable Integer doctorId, Model model, Authentication authentication) {
        DoctorDTO doctor = doctorDAO.findById(doctorId);
        UserEntity user = userRepository.findByUserName(authentication.getName());
        PatientDTO patient = patientDAO.findByEmail(user.getEmail());
        String username = authentication.getName();
        List<WorkingHours> workingHoursList = doctorService.getWorkingHours(doctorId);
        List<ReservationDTO> allReservationsByDoctor = reservationService.findAllReservationsByDoctor(doctorId);

        model.addAttribute("doctor", doctor);
        model.addAttribute("patientId", patient.getPatientId());
        model.addAttribute("username", username);

        boolean ifDoctorWorkCurrentWeek = reservationService.checkIfDoctorWorkingInRestOfThisWeek(workingHoursList);
        model.addAttribute("ifDoctorWorkCurrentWeek", ifDoctorWorkCurrentWeek);
        if (ifDoctorWorkCurrentWeek) {
            Map<String, WorkingHours> currentWeek = reservationService.currentWeek(workingHoursList);
            currentWeek = reservationService.afterCheck(currentWeek, allReservationsByDoctor);
            model.addAttribute("currentWeek", currentWeek);
        }

        Map<String, WorkingHours> nextWeek = reservationService.nextWeek(workingHoursList);
        nextWeek = reservationService.afterCheck(nextWeek, allReservationsByDoctor);
        model.addAttribute("nextWeek", nextWeek);

        return "doctor/doctor-schedule-new";
    }

    // odczytywanie plików tekstowych z opisem lekarzy -> chyba nie będzie potrzebne
    private String readDescriptionFromFile(String fileName) throws IOException {
        return Files.readString(Paths.get(fileName));
    }


}
