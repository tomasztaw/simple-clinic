package pl.taw.business;

import lombok.AllArgsConstructor;
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

    public List<VisitDTO> findAllVisitByPatient(Integer patientId) {
        return visitRepository.findAllByPatient(patientId);
    }

    public List<VisitDTO> findAllVisitForBoth(Integer doctorId, Integer patientId) {
        return visitRepository.findAll().stream()
                .filter(visit -> doctorId.equals(visit.getDoctor().getDoctorId()))
                .filter(visit -> patientId.equals(visit.getPatient().getPatientId()))
                .toList();
    }

    public boolean hasPatientSeenThisDoctor(Integer doctorId, Integer patientId) {
        return findAllVisitForBoth(doctorId, patientId).size() > 0;
    }

}
