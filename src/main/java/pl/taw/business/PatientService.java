package pl.taw.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.taw.business.dao.PatientDAO;

import java.util.Locale;
import java.util.Map;

@Service
@AllArgsConstructor
public class PatientService {

    private final PatientDAO patientDAO;

    public Map<Integer, String> getPatientsFullNamesAll() {
        return patientDAO.getPatientsFullNamesByIdAll();
    }

    public Map<Integer, String> getPatientsFullNamesForDoctor(Integer doctorId) {
        return patientDAO.getPatientsFullNamesByIdForDoctor(doctorId);
    }

    public String getBirthDate(String pesel) {
        int year = Integer.parseInt(pesel.substring(0, 2));
        int month = Integer.parseInt(pesel.substring(2, 4));
        int day = Integer.parseInt(pesel.substring(4, 6));

        String prefix = (month < 20) ? "19" : "20";
        if (month > 20) {
            month -= 20;
        }

        return String.format(Locale.US, "%s%02d-%02d-%02d", prefix, year, month, day);
    }

}
