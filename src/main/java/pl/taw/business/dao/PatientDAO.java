package pl.taw.business.dao;

import pl.taw.api.dto.PatientDTO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;

import java.util.List;

public interface PatientDAO {

    List<PatientDTO> findAll();

    PatientDTO findById(Integer patientId);

    PatientEntity findEntityById(Integer patientId);

    PatientDTO findByPesel(String pesel);

    PatientEntity saveAndReturn(PatientEntity patientEntity);

    void save(PatientEntity patientEntity);

    void delete(PatientEntity patientEntity);

}
