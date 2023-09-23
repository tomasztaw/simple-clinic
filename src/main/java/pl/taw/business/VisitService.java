package pl.taw.business;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.VisitEntity;

import java.util.List;

@Service
@AllArgsConstructor
public class VisitService {

    private final VisitDAO visitDAO;

    // #################### dodanie dla testów rest assured
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public VisitEntity saveVisit(VisitEntity visitEntity) {
        entityManager.persist(visitEntity);
        return visitEntity;
    }
    // ####################################################


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
        return !findAllVisitForBoth(doctorId, patientId).isEmpty();
    }

    public Page<VisitDTO> getVisitsPage(int page, int size) {
        Sort sort = Sort.by("visitId").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return visitDAO.findAll(pageable);
    }




}
