package pl.taw.business;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.taw.api.dto.DoctorScheduleDTO;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.DoctorScheduleDAO;
import pl.taw.business.dao.ReservationDAO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        return reservationDAO.findAll().stream()
                .filter(reservation -> doctorId.equals(reservation.getDoctorId()))
                .filter(reservation -> patientId.equals(reservation.getPatientId()))
                .toList();
    }

    public List<ReservationDTO> findAllByDay(LocalDate day) {
        return reservationDAO.findAllByDay(day);
    }


    // Próba uproszczenia mapy - działa, używam
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

    public void createReservation(ReservationDTO reservationDTO) {
        // Tutaj możesz umieścić logikę tworzenia rezerwacji
        // Na przykład, walidacja danych, sprawdzanie dostępności lekarza, itp.
        // Możesz również użyć repozytorium (np. ReservationRepository) do zapisu rezerwacji w bazie danych.
    }

    public void cancelReservation(Integer reservationId) {
        // Tutaj możesz umieścić logikę odwoływania rezerwacji
        // Możesz użyć repozytorium do pobrania rezerwacji na podstawie identyfikatora i zastosować odpowiednie operacje.
    }

    public List<LocalTime> getAvailableTimes(Integer doctorId, LocalDate day) {
        // Tutaj możesz umieścić logikę pobierania dostępnych terminów rezerwacji
        // Na podstawie identyfikatora lekarza i daty, możesz sprawdzić zajętość w danym dniu i zwrócić listę dostępnych godzin.
        // Możesz użyć repozytorium do pobrania rezerwacji dla danego lekarza i dnia, a następnie zastosować odpowiednie operacje.
        // Na przykład, możesz zwrócić listę godzin, które nie są zajęte na podstawie istniejących rezerwacji.
//        return availableTimes;
        return Collections.emptyList();
    }




}
