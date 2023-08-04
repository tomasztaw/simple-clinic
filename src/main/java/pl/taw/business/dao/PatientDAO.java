package pl.taw.business.dao;

import pl.taw.api.dto.PatientDTO;
import pl.taw.infrastructure.database.entity.PatientEntity;

import java.util.List;

public interface PatientDAO {

    List<PatientDTO> findAll();

    PatientDTO findById(Integer patientId);

    PatientEntity findEntityById(Integer patientId);

    PatientDTO findByPesel(String pesel);

    PatientDTO findByEmail(String email);

    PatientEntity saveAndReturn(PatientEntity patientEntity);

    void save(PatientEntity patientEntity);

    void delete(PatientEntity patientEntity);

}
