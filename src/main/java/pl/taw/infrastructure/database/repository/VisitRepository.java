package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.infrastructure.database.repository.jpa.VisitJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.VisitMapper;

import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class VisitRepository implements VisitDAO {

    private final VisitJpaRepository visitJpaRepository;
    private final VisitMapper visitMapper;


    @Override
    public List<VisitDTO> findAll() {
        log.debug("Fetching all visits from the database");
        return visitJpaRepository.findAll().stream()
                .map(visitMapper::mapFromEntity)
                .toList();
    }

    @Override
    public Page<VisitDTO> findAll(Pageable pageable) {
        Page<VisitEntity> visitPage = visitJpaRepository.findAll(pageable);
        return visitPage.map(visitMapper::mapFromEntity);
    }

    @Override
    public VisitDTO findById(Integer visitId) {
        return visitJpaRepository.findById(visitId)
                .map(visitMapper::mapFromEntity)
                .orElseThrow(() -> new NotFoundException("Could not found visit with id: [%s]".formatted(visitId)));
    }

    @Override
    public VisitEntity findEntityById(Integer visitId) {
        return visitJpaRepository.findById(visitId)
                .orElseThrow(() -> new NotFoundException("Could not found visit with id: [%s]".formatted(visitId)));
    }

    @Override
    public VisitEntity saveAndReturn(VisitEntity visitEntity) {
        return visitJpaRepository.save(visitEntity);
    }

    @Override
    public void save(VisitEntity visitEntity) {
        visitJpaRepository.save(visitEntity);
    }

    @Override
    public void delete(VisitEntity visitEntity) {
        visitJpaRepository.delete(visitEntity);
    }

    @Override
    public List<VisitDTO> findAllByDoctor(Integer doctorId) {
        return  visitJpaRepository.findAll().stream()
                .filter(visit -> visit.getDoctor().getDoctorId().equals(doctorId))
                .map(visitMapper::mapFromEntity)
                .toList();
    }

    @Override
    public List<VisitDTO> findAllByPatient(Integer patientId) {
        return  visitJpaRepository.findAll().stream()
                .filter(visit -> visit.getPatient().getPatientId().equals(patientId))
                .map(visitMapper::mapFromEntity)
                .toList();
    }

    @Override
    public List<VisitDTO> findAllForBoth(Integer doctorId, Integer patientId) {
        return visitJpaRepository.findAll().stream()
                .filter(visit -> doctorId.equals(visit.getDoctorId()) && patientId.equals(visit.getPatientId()))
                .map(visitMapper::mapFromEntity)
                .toList();
    }
}
