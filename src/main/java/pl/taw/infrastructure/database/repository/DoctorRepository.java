package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.repository.jpa.DoctorJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.DoctorMapper;

import java.util.List;

@Repository
@AllArgsConstructor
public class DoctorRepository implements DoctorDAO {

    private final DoctorJpaRepository doctorJpaRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public List<DoctorDTO> findAll() {
        return doctorJpaRepository.findAll().stream()
                .map(doctorMapper::mapFromEntity)
                .toList();
    }

    @Override
    public List<DoctorDTO> findBySpecialization(String specialization) {
        return doctorJpaRepository.findAll().stream()
                .filter(doctor -> doctor.getTitle().equals(specialization))
                .map(doctorMapper::mapFromEntity)
                .toList();
    }

    @Override
    public DoctorDTO findById(Integer doctorId) {
        return doctorJpaRepository.findById(doctorId)
                .map(doctorMapper::mapFromEntity)
                .orElseThrow(() -> new NotFoundException(
                        "Could not found doctorEntity with id: [%s]".formatted(doctorId)));
    }

    @Override
    public DoctorEntity findEntityById(Integer doctorId) {
        return doctorJpaRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException(
                        "Could not found doctorEntity with id: [%s]".formatted(doctorId)));
    }

    @Override
    public DoctorEntity saveAndReturn(DoctorEntity doctorEntity) {
        return doctorJpaRepository.save(doctorEntity);
    }

    @Override
    public void save(DoctorEntity doctorEntity) {
        doctorJpaRepository.save(doctorEntity);
    }

    @Override
    public void delete(DoctorEntity doctorEntity) {
        doctorJpaRepository.delete(doctorEntity);
    }


}
