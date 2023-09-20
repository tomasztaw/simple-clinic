package pl.taw.business;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.springframework.stereotype.Service;
import pl.taw.api.dto.DoctorScheduleDTO;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.business.dao.DoctorScheduleDAO;
import pl.taw.business.dao.ReservationDAO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationDAO reservationDAO;
    private final DoctorScheduleDAO doctorScheduleDAO;
    private final DoctorService doctorService;


    public List<ReservationDTO> findAllReservationsByPatient(Integer patientId) {
        return reservationDAO.findAllByPatient(patientId);
    }

    public List<ReservationDTO> findAllReservationsByDoctor(Integer doctorId) {
        return reservationDAO.findAllByDoctor(doctorId);
    }

    public List<ReservationDTO> findAllReservationsForBoth(Integer doctorId, Integer patientId) {
        return reservationDAO.findAllByBoth(doctorId, patientId);
    }

    public List<ReservationDTO> findAllByDay(LocalDate day) {
        return reservationDAO.findAllByDay(day);
    }


    public Map<String, WorkingHours> simpleMapForNextWeek(List<WorkingHours> doctorWorkingHours) {
        LocalDate today = LocalDate.now();
        Map<String, LocalDate> nextWeekLeftDays = new LinkedHashMap<>();
        LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        List<LocalDate> weekDaysList = new ArrayList<>();
        Map<String, LocalDate> weekDaysMap = new HashMap<>();

        for (int i = 0; i < 5; i++) {
            LocalDate day = nextMonday.plusDays(i);
            weekDaysList.add(day);
            weekDaysMap.put(day.getDayOfWeek().toString(), day);
            for (WorkingHours hours : doctorWorkingHours) {
                if (day.getDayOfWeek().getValue() == hours.getDayOfTheWeek().getNumber()) {
                    nextWeekLeftDays.put(WorkingHours.DayOfTheWeek.fromInt(day.getDayOfWeek().getValue()).getName(), day);
                }
            }
        }

        List<String> dayAndDate = nextWeekLeftDays.entrySet().stream()
                .map(entry -> entry.getKey() + " " + entry.getValue())
                .toList();

        // dzień tygodnia i data, godziny pracy
        Map<String, WorkingHours> resultMap = IntStream.range(0, Math.min(doctorWorkingHours.size(), dayAndDate.size()))
                .boxed()
                .collect(Collectors.toMap(dayAndDate::get, doctorWorkingHours::get, (a, b) -> b, LinkedHashMap::new));

        return resultMap;
    }


    // Próba uproszczenia mapy - działa, używam - pokazuje obecny tydzień od następnego dnia roboczego
    public Map<String, WorkingHours> simpleMap(List<WorkingHours> doctorWorkingHours) {

        Map<String, WorkingHours> result = new LinkedHashMap<>();

        // dzisiejszy dzień
        LocalDate today = LocalDate.now();

        // pozostałe dni nie wliczając dzisiaj
        List<WorkingHours.DayOfTheWeek> nextDays = doctorWorkingHours.stream()
                .map(WorkingHours::getDayOfTheWeek)
                .dropWhile(day -> day.getNumber() <= today.getDayOfWeek().getValue())
                .toList();

        // następne dni z datą
        Map<String, LocalDate> currentWeekLeftDays = new LinkedHashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.getValue() > today.getDayOfWeek().getValue() && day.getValue() < 6) {
                currentWeekLeftDays.put(day.name(), today.with(DayOfWeek.valueOf(day.name())));
            }
        }

        List<WorkingHours> list = doctorWorkingHours.stream()
                .filter(day -> day.getDayOfTheWeek().getNumber() > today.getDayOfWeek().getValue())
                .toList();

        List<String> dayAndDate = currentWeekLeftDays.entrySet().stream()
                .filter(entry -> list.stream()
                        .anyMatch(day -> day.getDayOfTheWeek().getNumber()
                                        == DayOfWeek.valueOf(entry.getKey()).getValue()))
                .map(entry -> entry.getKey() + " " + entry.getValue())
                .toList();

        Map<String, WorkingHours> resultMap = IntStream.range(0, Math.min(list.size(), dayAndDate.size()))
                .boxed()
                .collect(Collectors.toMap(dayAndDate::get, list::get, (a, b) -> b, LinkedHashMap::new));

        return resultMap;
    }



    // Mapa zawiera wszystkie potrzebne informacje, ale nie mogę w odpowiedni sposób jej wyświetlić
    public Map<Map<String, LocalDate>, List<WorkingHours>> getWorkingHoursForCurrentWeek(List<WorkingHours> doctorWorkingHours) {

        List<WorkingHours> result = new ArrayList<>();

        // dzisiejszy dzień
        LocalDate today = LocalDate.now();

        // pozostałe dni nie wliczając dzisiaj
        List<WorkingHours.DayOfTheWeek> nextDays = doctorWorkingHours.stream()
                .map(WorkingHours::getDayOfTheWeek)
                .dropWhile(day -> day.getNumber() <= today.getDayOfWeek().getValue())
                .toList();

        // następne dni z datą
        Map<String, LocalDate> currentWeekLeftDays = new LinkedHashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.getValue() > today.getDayOfWeek().getValue() && day.getValue() < 6) {
                currentWeekLeftDays.put(day.name(), today.with(DayOfWeek.valueOf(day.name())));
            }
        }

        List<WorkingHours> list = doctorWorkingHours.stream()
                .filter(day -> day.getDayOfTheWeek().getNumber() > today.getDayOfWeek().getValue())
                .toList();

        Map<Map<String, LocalDate>, List<WorkingHours>> resultMap = new HashMap<>();
        resultMap.put(currentWeekLeftDays, list);

        return resultMap;
    }

    // działa, ale nie wiem czy mam sens dodatkowe komplikowanie dla wyświetlenia bieżącego dnia.
    public String isDoctorWorkingToDay(List<WorkingHours> doctorWorkingHours) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter hoursFormat = DateTimeFormatter.ofPattern("HH:mm");
        String currentHour = currentTime.format(hoursFormat);

        for (WorkingHours workingDay : doctorWorkingHours) {
            // jeżeli dzisiaj pracuje i jest przed końcem jego zmiany
            if (workingDay.getDayOfTheWeek().getNumber() == currentTime.getDayOfWeek().getValue()
                    && currentTime.toLocalTime().isBefore(workingDay.getEndTime())) {

                // znajdź pierwszą pasującą godzinę
                var firstTime = doctorWorkingHours.stream()
                        .map(WorkingHours::getAppointmentTimes)
                        .flatMap(Collection::stream)
                        .dropWhile(item -> item.compareTo(currentHour) <= 0)
                        .findFirst();

                // lista pasujących terminów dzisiaj
                var availableSlotOfTimesToday = doctorWorkingHours.stream()
                        .filter(hours -> hours.getDayOfTheWeek().getNumber() == currentTime.getDayOfWeek().getValue())
                        .map(WorkingHours::getAppointmentTimes)
                        .flatMap(Collection::stream)
                        .dropWhile(slot -> slot.compareTo(currentHour) <= 0)
                        .toList();

                // do której lekarz dzisiaj pracuje
                var endOfShiftForToday = doctorWorkingHours.stream()
                        .filter(day -> day.getDayOfTheWeek().getNumber() == currentTime.getDayOfWeek().getValue())
                        .map(WorkingHours::getEndTime)
                        .findFirst()
                        .orElseThrow();

                String endTime = endOfShiftForToday.format(hoursFormat);

                String answer = String.format("""
                        Lekarz pracuje dzisiaj do %s.
                        Dostępne terminy to %s""", endTime, availableSlotOfTimesToday);

                // zwróć dostępne terminy
                return answer;
            }
        }
        // zwróć najbliższy dostępny dzień z terminami
        return "Lekarz nie pracuje dzisiaj";
    }

    public List<WorkingHours> getNextAvailableDay(List<WorkingHours> doctorWorkingHours) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter hoursFormat = DateTimeFormatter.ofPattern("HH:mm");
        String currentHour = currentTime.format(hoursFormat);

//        DayOfWeek nextDay = doctorWorkingHours.indexOf(currentTime.getDayOfWeek().getValue() + 1);
        int nextDayInt = doctorWorkingHours.indexOf(currentTime.getDayOfWeek().getValue() + 1);

        List<WorkingHours.DayOfTheWeek> daysInWeek = doctorWorkingHours.stream()
                .map(WorkingHours::getDayOfTheWeek)
                .toList();

        return null;
    }

    // nie wiem - jakieś próby
    public LocalDate getNearestDayOfDoctorWorking(Integer doctorId) {
        LocalDateTime currentTime = LocalDateTime.now();

        LocalDate today = LocalDate.now();
        String todayFormat = today.format(DateTimeFormatter.ofPattern("dd MM yyyy"));
        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();
        String dzisiaj = WorkingHours.DayOfTheWeek.fromInt(today.getDayOfWeek().getValue()).getName();
        List<DoctorScheduleDTO> schedules = doctorScheduleDAO.findScheduleByDoctorId(doctorId);

        Optional<DoctorScheduleDTO> first = schedules.stream()
                .filter(sch -> sch.getDayOfTheWeek().equals(currentTime.getDayOfWeek().getValue()))
                .findFirst();

        LocalDate firstDay;
        WorkingHours.DayOfTheWeek firstDayOfTheWeek;

        List<WorkingHours> workingHours = doctorService.getWorkingHours(doctorId);

        for (WorkingHours day : workingHours) {
            if (currentDayOfWeek.getValue() == currentDayOfWeek.getValue()) {
                firstDayOfTheWeek = day.getDayOfTheWeek();
            }
        }

        return null;
    }

    // ################### nowe wyświetlanie dostępnych terminów

    public Map<String, WorkingHours> showNextWeek(List<WorkingHours> doctorWorkingHours) {
        LocalDate today = LocalDate.now();

        Map<String, LocalDate> nextWeekDays = new LinkedHashMap<>();
        LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        List<LocalDate> weekDaysList = new ArrayList<>();
        Map<String, LocalDate> weekDaysMap = new HashMap<>();

        for (int i = 0; i < 5; i++) {
            LocalDate day = nextMonday.plusDays(i);
            weekDaysList.add(day);
//            weekDaysMap.put(day.getDayOfWeek().toString(), day);
            weekDaysMap.put(WorkingHours.DayOfTheWeek.fromInt(day.getDayOfWeek().getValue()).getName(), day);
            for (WorkingHours hours : doctorWorkingHours) {
                if (day.getDayOfWeek().getValue() == hours.getDayOfTheWeek().getNumber()) {
                    nextWeekDays.put(WorkingHours.DayOfTheWeek.fromInt(day.getDayOfWeek().getValue()).getName(), day);
                }
            }
        }

        List<String> dayAndDate = nextWeekDays.entrySet().stream()
                .map(entry -> entry.getKey() + " " + entry.getValue())
                .toList();

        // dzień tygodnia i data, godziny pracy
        Map<String, WorkingHours> resultMap = IntStream.range(0, Math.min(doctorWorkingHours.size(), dayAndDate.size()))
                .boxed()
                .collect(Collectors.toMap(dayAndDate::get, doctorWorkingHours::get, (a, b) -> b, LinkedHashMap::new));

        return resultMap;
    }

    public boolean checkIfDoctorWorkingInRestOfThisWeek(List<WorkingHours> doctorWorkingHours) {
        LocalDate today = LocalDate.now();
        int dayNum = today.getDayOfWeek().getValue();
        if (dayNum >= 5) {
            return false;
        }
        long count = doctorWorkingHours.stream()
                .map(day -> day.getDayOfTheWeek().getNumber())
                .filter(num -> num > dayNum)
                .count();
        return count > 0;
    }

    public boolean checkIfDoctorWorkingInRestOfThisWeekGPT(List<WorkingHours> doctorWorkingHours) {
        LocalDate today = LocalDate.now();
        DayOfWeek currentDay = today.getDayOfWeek();

        if (currentDay == DayOfWeek.FRIDAY || currentDay == DayOfWeek.SATURDAY) {
            return false;
        }

        int daysRemaining = DayOfWeek.values().length - currentDay.getValue() - 1;

        for (int i = 1; i <= daysRemaining; i++) {
            DayOfWeek nextDay = currentDay.plus(i);

            int nextDayNumber = nextDay.getValue();

            if (doctorWorkingHours.stream().anyMatch(
                    wh -> wh.getDayOfTheWeek().getNumber() == nextDayNumber)) {
                return true;
            }
        }
        return false;
    }


    public Map<String, WorkingHours> currentWeek(List<WorkingHours> doctorWorkingHours) {
        LocalDate today = LocalDate.now();

        List<String> nextDaysPolName = this.getPolNameForRestWeekDays(today, doctorWorkingHours);

        Map<Integer, String> currentAlternative = this.leftDays(today);

        List<WorkingHours> workingHoursList = this.workingHoursForRestWeek(today, doctorWorkingHours);

        List<String> fullDateAlt = this.getFullDAte(currentAlternative, workingHoursList);

        Map<String, WorkingHours> resultAlt = IntStream.range(0, Math.min(workingHoursList.size(), fullDateAlt.size()))
                .boxed()
                .collect(Collectors.toMap(fullDateAlt::get,
                        workingHoursList::get,
                        (a, b) -> b,
                        LinkedHashMap::new));

        return resultAlt;
    }

    public List<String> getFullDAte(Map<Integer, String> currentWeekDays, List<WorkingHours> workingHoursList) {
        return currentWeekDays.entrySet().stream()
                .filter(entry -> workingHoursList.stream()
                        .anyMatch(day -> day.getDayOfTheWeek().getNumber() == entry.getKey()))
                .map(entry ->
                        WorkingHours.DayOfTheWeek.fromInt(entry.getKey()).getName()
                                + " " + entry.getValue())
                .toList();
    }

    public List<WorkingHours> workingHoursForRestWeek(
            LocalDate today, List<WorkingHours> doctorWorkingHours) {
        return doctorWorkingHours.stream()
                .filter(day -> day.getDayOfTheWeek().getNumber() > today.getDayOfWeek().getValue())
                .toList();
    }

    public List<String> getPolNameForRestWeekDays(
            LocalDate today, List<WorkingHours> doctorWorkingHours
    ) {
        return doctorWorkingHours.stream()
                .map(WorkingHours::getDayOfTheWeek)
                .dropWhile(day -> day.getNumber() <= today.getDayOfWeek().getValue())
                .map(day -> WorkingHours.DayOfTheWeek.fromInt(day.getNumber()).getName())
                .toList();
    }

    public Map<Integer, String> leftDays(LocalDate today) {
        Map<Integer, String> result = new LinkedHashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.getValue() > today.getDayOfWeek().getValue() && day.getValue() < 6) {
                result.put(day.getValue(), today.with(DayOfWeek.valueOf(day.name())).toString());
            }
        }
        return result;
    }

    public Map<Integer, String> leftDaysFun(LocalDate today) {
        return Stream.of(DayOfWeek.values())
                .filter(day -> day.getValue() > today.getDayOfWeek().getValue() && day.getValue() < 6)
                .collect(Collectors.toMap(DayOfWeek::getValue,
                        day -> today.with(DayOfWeek.valueOf(day.name())).toString(),
                        (a, b) -> b, LinkedHashMap::new));
    }

    public List<String> daysLeft(List<WorkingHours> doctorWorkingHours) {
        LocalDate today = LocalDate.now();
        return doctorWorkingHours.stream()
                .map(WorkingHours::getDayOfTheWeek)
                .dropWhile(day -> day.getNumber() <= today.getDayOfWeek().getValue())
                .map(day -> WorkingHours.DayOfTheWeek.fromInt(day.getNumber()).toString())
                .toList();
    }

    public List<String> daysLeftPol(List<WorkingHours> doctorWorkingHours) {
        LocalDate today = LocalDate.now();

        return doctorWorkingHours.stream()
                .map(WorkingHours::getDayOfTheWeek)
                .dropWhile(day -> day.getNumber() <= today.getDayOfWeek().getValue())
                .map(day -> WorkingHours.DayOfTheWeek.fromInt(day.getNumber()).getName())
                .toList();
    }
}
