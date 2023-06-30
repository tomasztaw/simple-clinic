package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.taw.api.dto.PatientDTO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.jpa.PatientJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.PatientMapper;

import java.util.List;

@Repository
@AllArgsConstructor
public class PatientRepository implements PatientDAO {

    private final PatientJpaRepository patientJpaRepository;
    private final PatientMapper patientMapper;

    @Override
    public List<PatientDTO> findAll() {
        return patientJpaRepository.findAll().stream()
                .map(patientMapper::mapFromEntity)
                .toList();
    }

    @Override
    public PatientDTO findById(Integer patientId) {
        return patientJpaRepository.findById(patientId)
                .map(patientMapper::mapFromEntity)
                .orElseThrow(() -> new NotFoundException(
                        "Could not found patientEntity with id: [%s]".formatted(patientId)));
    }

    @Override
    public PatientEntity findEntityById(Integer patientId) {
        return patientJpaRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException(
                        "Could not found patientEntity with id: [%s]".formatted(patientId)));
    }

    @Override
    public PatientDTO findByPesel(String pesel) {
        return patientJpaRepository.findAll().stream()
                .filter(patient -> pesel.equals(patient.getPesel()))
                .findFirst()
                .map(patientMapper::mapFromEntity)
                .orElseThrow(() -> new NotFoundException(
                        "Could not found patientEntity with pesel: [%s]".formatted(pesel)));
    }

    @Override
    public PatientDTO findByEmail(String email) {
        return patientJpaRepository.findAll().stream()
                .filter(patient -> email.equals(patient.getEmail()))
                .findFirst()
                .map(patientMapper::mapFromEntity)
                .orElseThrow(() -> new NotFoundException(
                        "Could not found patientEntity with email: [%s]".formatted(email)));
    }

    @Override
    public PatientEntity saveAndReturn(PatientEntity patientEntity) {
        return patientJpaRepository.save(patientEntity);
    }

    @Override
    public void save(PatientEntity patientEntity) {
        patientJpaRepository.save(patientEntity);
    }

    @Override
    public void delete(PatientEntity patientEntity) {
        patientJpaRepository.delete(patientEntity);
    }
}
