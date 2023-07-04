package pl.taw.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.DoctorScheduleDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.business.dao.DoctorScheduleDAO;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DoctorService {

    private final DoctorDAO doctorDAO;

    private final DoctorScheduleDAO doctorScheduleDAO;

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

}
