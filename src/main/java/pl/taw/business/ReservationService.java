package pl.taw.business;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.taw.api.dto.DoctorScheduleDTO;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.business.dao.DoctorScheduleDAO;
import pl.taw.business.dao.ReservationDAO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationDAO reservationDAO;
    private final DoctorScheduleDAO doctorScheduleDAO;
    private final DoctorService doctorService;

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
