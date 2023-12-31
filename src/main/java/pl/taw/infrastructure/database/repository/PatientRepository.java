package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.taw.api.dto.PatientDTO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.jpa.PatientJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.PatientMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if (!patientJpaRepository.existsByPesel(patientEntity.getPesel())
                && !patientJpaRepository.existsByEmail(patientEntity.getEmail())) {
            return patientJpaRepository.saveAndFlush(patientEntity);
        } else {
            String answer = patientJpaRepository.existsByPesel(patientEntity.getPesel()) ? "peselem" : "emailem";
            throw new RuntimeException("W systemie znajduje się już ktoś z takim %s".formatted(answer));
        }
    }

    @Override
    public void save(PatientEntity patientEntity) {
        if (!patientJpaRepository.existsByPesel(patientEntity.getPesel())
                && !patientJpaRepository.existsByEmail(patientEntity.getEmail())) {
            patientJpaRepository.save(patientEntity);
        } else {
            throw new RuntimeException("W systemie znajduje się już ktoś z takim peselem/emailem");
        }
    }

    @Override
    public void saveForUpdateContact(PatientEntity patientEntity) {
        patientJpaRepository.save(patientEntity);
    }

    @Override
    public void delete(PatientEntity patientEntity) {
        patientJpaRepository.delete(patientEntity);
    }

    @Override
    public Map<Integer, String> getPatientsFullNamesByIdAll() {
        return patientJpaRepository.findAll().stream()
                .collect(Collectors.toMap(
                        PatientEntity::getPatientId,
                        val -> val.getName().concat(" ").concat(val.getSurname())));
    }

    @Override
    public Map<Integer, String> getPatientsFullNamesByIdForDoctor(Integer doctorId) {
        return null;
    }
}
