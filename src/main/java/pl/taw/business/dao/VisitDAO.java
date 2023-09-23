package pl.taw.business.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.entity.VisitEntity;

import java.util.List;

public interface VisitDAO {

    List<VisitDTO> findAll();

    Page<VisitDTO> findAll(Pageable pageable);

    VisitDTO findById(Integer visitId);

    VisitEntity findEntityById(Integer visitId);

    VisitEntity saveAndReturn(VisitEntity visitEntity);

    void save(VisitEntity visitEntity);

    void delete(VisitEntity visitEntity);

    List<VisitDTO> findAllByDoctor(Integer doctorId);

    List<VisitDTO> findAllByPatient(Integer patientId);

    List<VisitDTO> findAllForBoth(Integer doctorId, Integer patientId);

    List<PatientDTO> findAllThisDoctorPatients(Integer doctorId);


}
