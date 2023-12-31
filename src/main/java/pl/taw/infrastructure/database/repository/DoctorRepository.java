package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.PatientDTO;
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
                .filter(doctor -> doctor.getTitle().equalsIgnoreCase(specialization))
                .map(doctorMapper::mapFromEntity)
                .toList();
    }

    @Override
    public List<String> findAllSpecializations() {
        return doctorJpaRepository.findAll().stream()
                .map(DoctorEntity::getTitle)
                .distinct()
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
    public DoctorDTO findByEmail(String email) {
        return doctorJpaRepository.findAll().stream()
                .filter(doctor -> email.equals(doctor.getEmail()))
                .findFirst()
                .map(doctorMapper::mapFromEntity)
                .orElseThrow(() -> new NotFoundException(
                        "Could not fount doctorEntity with email: [%s]".formatted(email)));
    }

    @Override
    public DoctorEntity saveAndReturn(DoctorEntity doctorEntity) {
        return doctorJpaRepository.saveAndFlush(doctorEntity);
    }

    @Override
    public void save(DoctorEntity doctorEntity) {
        doctorJpaRepository.save(doctorEntity);
    }

    @Override
    public void delete(DoctorEntity doctorEntity) {
        doctorJpaRepository.delete(doctorEntity);
    }

    // dodane dla rest api, zobaczymy co dalej
    @Override
    public void deleteById(Integer doctorId) {
        doctorJpaRepository.deleteById(doctorId);
    }

    // dodane dla testów, zobaczymy co dalej
    public void saveAllAndFlush(List<DoctorEntity> doctorEntities) {
        doctorJpaRepository.saveAllAndFlush(doctorEntities);
    }

    @Override
    public Page<DoctorDTO> findAll(Pageable pageable) {
        Page<DoctorEntity> doctorPage = doctorJpaRepository.findAll(pageable);
        return doctorPage.map(doctorMapper::mapFromEntity);
    }

}
