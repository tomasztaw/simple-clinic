package pl.taw.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.repository.VisitRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class VisitService {

    private final VisitDAO visitDAO;


    public List<VisitDTO> findAllVisitByDoctor(Integer doctorId) {
        return visitDAO.findAllByDoctor(doctorId);
    }

    public List<VisitDTO> findAllVisitByPatient(Integer patientId) {
        return visitDAO.findAllByPatient(patientId);
    }

    public List<VisitDTO> findAllVisitForBoth(Integer doctorId, Integer patientId) {
        return visitDAO.findAllForBoth(doctorId, patientId);
    }

    public boolean hasPatientSeenThisDoctor(Integer doctorId, Integer patientId) {
        return findAllVisitForBoth(doctorId, patientId).size() > 0;
    }

}
