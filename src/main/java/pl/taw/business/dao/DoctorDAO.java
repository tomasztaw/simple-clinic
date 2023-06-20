package pl.taw.business.dao;

import pl.taw.api.dto.DoctorDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;

import java.util.List;

public interface DoctorDAO {

    List<DoctorDTO> findAll();

    DoctorDTO findById(Integer doctorId);

    DoctorEntity findEntityById(Integer doctorId);

    DoctorEntity saveAndReturn(DoctorEntity doctorEntity);

    void save(DoctorEntity doctorEntity);

    void delete(DoctorEntity doctorEntity);
}
