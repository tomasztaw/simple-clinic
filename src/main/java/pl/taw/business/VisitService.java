package pl.taw.business;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.repository.VisitRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;


    public List<VisitDTO> findAllVisitByDoctor(Integer doctorId) {
        return visitRepository.findAllByDoctor(doctorId);
    }

    public List<VisitDTO> findAllByPatient(Integer patientId) {
        return visitRepository.findAllByPatient(patientId);
    }

}
