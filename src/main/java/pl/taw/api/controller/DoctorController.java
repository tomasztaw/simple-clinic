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
import pl.taw.api.dto.PatientDTO;
import pl.taw.business.DoctorService;
import pl.taw.business.ReservationService;
import pl.taw.business.ScheduleEntry;
import pl.taw.business.WorkingHours;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.repository.jpa.DoctorJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.DoctorMapper;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
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


    private final DoctorDAO doctorDAO;
    private final DoctorService doctorService;
    private final ReservationService reservationService;


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
        String doctorDescFile = "src/main/resources/desc/doctorDesc" + doctorId + ".txt";
//        String defaultDescription = "src/main/resources/desc/doctorDesc0.txt";
        String defaultDescription = "src/main/resources/desc/default.rtf";
        Path filePath = Paths.get(doctorDescFile);
        String description;
        try {
            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                description = new String(Files.readAllBytes(filePath));
            } else {
                description = new String(Files.readAllBytes(Paths.get(defaultDescription)));
            }
            model.addAttribute("description", description);
        } catch (IOException e) {
            throw new RuntimeException("Brak pliku");
        }
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
    @GetMapping("/stare/{doctorId}")  // coś się jeszcze z tego wykorzysta !!!
    public String getDoctorSchedule(@PathVariable Integer doctorId, Model model) {
        DoctorDTO doctor = doctorDAO.findById(doctorId);
        List<WorkingHours> workingHoursList = doctorService.getWorkingHours(doctorId);

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        String todayFormat = today.format(DateTimeFormatter.ofPattern("dd MM yyyy"));
        DateTimeFormatter hoursFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String czas = currentTime.format(hoursFormatter);
        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();
        String dzisiaj = WorkingHours.DayOfTheWeek.fromInt(today.getDayOfWeek().getValue()).getName();

        String czyPracuje = reservationService.isDoctorWorkingToDay(workingHoursList);

        List<WorkingHours.DayOfTheWeek> daysInWeek = workingHoursList.stream()
                .map(WorkingHours::getDayOfTheWeek)
                .toList();

        List<WorkingHours.DayOfTheWeek> nextDays = workingHoursList.stream()
                .map(WorkingHours::getDayOfTheWeek)
                .dropWhile(day -> day.getNumber() <= currentDayOfWeek.getValue())
                .toList();

        List<LocalDate> dateOfDaysInCurrentWeek = new ArrayList<>();

        Map<String, LocalDate> currentWeekDates = new HashMap<>();

        Map<String, LocalDate> currentWeekLeftDays = new LinkedHashMap<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            dateOfDaysInCurrentWeek.add(today.with(DayOfWeek.of(day.getValue())));

            currentWeekDates.put(day.name(), today.with(DayOfWeek.valueOf(day.name())));

            if (day.getValue() > currentTime.getDayOfWeek().getValue() && day.getValue() < 6) {
                currentWeekLeftDays.put(day.name(), today.with(DayOfWeek.valueOf(day.name())));
            }
        }

        model.addAttribute("workingHoursList", workingHoursList);
        model.addAttribute("doctor", doctor);

        model.addAttribute("currentTime", currentTime);
        model.addAttribute("todayFormat", todayFormat);
        model.addAttribute("currentDayOfWeek", currentDayOfWeek);
        model.addAttribute("dzisiaj", dzisiaj);
        model.addAttribute("czas", czas);

        model.addAttribute("czyPracuje", czyPracuje);
        model.addAttribute("daysInWeek", daysInWeek);
        model.addAttribute("nextDays", nextDays);

        model.addAttribute("dateOfDaysInCurrentWeek", dateOfDaysInCurrentWeek);
        model.addAttribute("currentWeekDates", currentWeekDates);
        model.addAttribute("currentWeekLeftDays", currentWeekLeftDays);

        return "doctor/doctor-schedule";
    }

    // widok dla obecnego tygodnia
    @GetMapping("/obecny/{doctorId}")
    public String getDoctorScheduleForCurrentWeek(@PathVariable Integer doctorId, Model model) {
        DoctorDTO doctor = doctorDAO.findById(doctorId);
        List<WorkingHours> workingHoursList = doctorService.getWorkingHours(doctorId);

        Map<Map<String, LocalDate>, List<WorkingHours>> workHours = reservationService.getWorkingHoursForCurrentWeek(workingHoursList);

        model.addAttribute("doctor", doctor);
        model.addAttribute("workHours", workHours);

        return "/doctor/doctor-schedule-2";
    }

    @GetMapping("/trzy/{doctorId}")
    public String getDoctorScheduleForCurrentWeekByEntry(@PathVariable Integer doctorId, Model model) {

        DoctorDTO doctor = doctorDAO.findById(doctorId);
        List<WorkingHours> workingHoursList = doctorService.getWorkingHours(doctorId);
        Map<Map<String, LocalDate>, List<WorkingHours>> resultMap = reservationService.getWorkingHoursForCurrentWeek(workingHoursList);


        List<ScheduleEntry> scheduleEntries = new ArrayList<>();
        for (Map.Entry<Map<String, LocalDate>, List<WorkingHours>> entry : resultMap.entrySet()) {
            Map<String, LocalDate> dates = entry.getKey();
            List<WorkingHours> workingHours = entry.getValue();

            for (Map.Entry<String, LocalDate> dateEntry : dates.entrySet()) {
                String dayOfWeek = dateEntry.getKey();
                LocalDate date = dateEntry.getValue();

                ScheduleEntry scheduleEntry = new ScheduleEntry();
                scheduleEntry.setDayOfWeek(dayOfWeek);
                scheduleEntry.setDate(date);
                scheduleEntry.setWorkingHours(workingHours);

                scheduleEntries.add(scheduleEntry);
            }
        }

        model.addAttribute("doctor", doctor);
        model.addAttribute("scheduleEntries", scheduleEntries);

        return "/doctor/doctor-schedule-3";
    }


    @GetMapping(SCHEDULE)
    public String getDoctorScheduleSimpleMap(@PathVariable Integer doctorId, Model model) {

        DoctorDTO doctor = doctorDAO.findById(doctorId);
        List<WorkingHours> workingHoursList = doctorService.getWorkingHours(doctorId);
        Map<Map<String, LocalDate>, List<WorkingHours>> resultMap = reservationService.getWorkingHoursForCurrentWeek(workingHoursList);

        Map<String, WorkingHours> simpleMap = reservationService.simpleMap(workingHoursList);


        model.addAttribute("doctor", doctor);
        model.addAttribute("simpleMap", simpleMap);

        // na razie przypiszemy pacjenta na sztywno
        model.addAttribute("patientId", 5);

        return "/doctor/doctor-schedule-3";
    }

    // odczytywanie plików tekstowych z opisem lekarzy
    private String readDescriptionFromFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }


}