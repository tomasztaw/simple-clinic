package pl.taw.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.taw.business.dao.PatientDAO;

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

}
