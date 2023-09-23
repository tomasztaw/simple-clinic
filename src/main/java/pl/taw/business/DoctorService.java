package pl.taw.business;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.DoctorScheduleDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.DoctorScheduleDAO;
import pl.taw.business.dao.VisitDAO;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DoctorService {

    private final DoctorDAO doctorDAO;

    private final DoctorScheduleDAO doctorScheduleDAO;

    private final VisitDAO visitDAO;

    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        return doctorDAO.findBySpecialization(specialization);
    }

    public List<String> getDoctorsSpecializations() {
        return doctorDAO.findAllSpecializations();
    }

    public List<WorkingHours> getWorkingHours(Integer doctorId) {
        List<DoctorScheduleDTO> doctorSchedules = doctorScheduleDAO.findScheduleByDoctorId(doctorId);
        return convertToWorkingHoursList(doctorSchedules);
    }

    private List<WorkingHours> convertToWorkingHoursList(List<DoctorScheduleDTO> doctorSchedules) {
        List<WorkingHours> workingHoursList = new ArrayList<>();

        for (DoctorScheduleDTO doctorSchedule : doctorSchedules) {
            WorkingHours workingHours = new WorkingHours();
            workingHours.setDayOfTheWeek(WorkingHours.DayOfTheWeek.fromInt(doctorSchedule.getDayOfTheWeek()));
            workingHours.setStartTime(doctorSchedule.getStartTimeDs());
            workingHours.setEndTime(doctorSchedule.getEndTimeDs());

            workingHours.setAppointmentTimes(generateAppointmentTimes(
                    workingHours.getStartTime(),
                    workingHours.getEndTime(),
                    workingHours.getIntervalMinutes()));

            workingHoursList.add(workingHours);
        }

        return workingHoursList;
    }

    private List<String> generateAppointmentTimes(LocalTime startTime, LocalTime endTime, int intervalMinutes) {
        List<String> appointmentTimes = new ArrayList<>();

        LocalTime currentTime = startTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        while (currentTime.isBefore(endTime)) {
            if (currentTime.getMinute() == 0 && currentTime != startTime) {
                // przerwa 10 minut co pełną godzinę
                currentTime = currentTime.plusMinutes(intervalMinutes);
            }
            appointmentTimes.add(currentTime.format(formatter));
            currentTime = currentTime.plusMinutes(intervalMinutes);
        }
        return appointmentTimes;
    }

    public Page<DoctorDTO> getDoctorsPage(int page, int size) {
        Sort sort = Sort.by("doctorId").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return doctorDAO.findAll(pageable);
    }

    public List<PatientDTO> patientsForThisDoctorList(Integer doctorId) {
        return visitDAO.findAllThisDoctorPatients(doctorId);
    }
}
