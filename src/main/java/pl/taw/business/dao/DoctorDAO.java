package pl.taw.business.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;

import java.util.List;

public interface DoctorDAO {

    List<DoctorDTO> findAll();

    Page<DoctorDTO> findAll(Pageable pageable);

    DoctorDTO findById(Integer doctorId);

    DoctorEntity findEntityById(Integer doctorId);

    DoctorEntity saveAndReturn(DoctorEntity doctorEntity);

    void save(DoctorEntity doctorEntity);

    void delete(DoctorEntity doctorEntity);

    void deleteById(Integer doctorId);

    List<DoctorDTO> findBySpecialization(String specialization);

    List<String> findAllSpecializations();
}
