package pl.taw.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;

import java.util.List;

@Service
@AllArgsConstructor
public class DoctorService {

    private final DoctorDAO doctorDAO;

    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        return doctorDAO.findBySpecialization(specialization);
    }

    public List<String> getDoctorSpecializations() {
        return doctorDAO.findAll().stream()
                .map(DoctorDTO::getTitle)
                .distinct()
                .toList();
    }
}
