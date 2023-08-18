package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.OpinionEntity;
import pl.taw.infrastructure.database.repository.jpa.OpinionJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.OpinionMapper;

import java.util.List;

@Repository
@AllArgsConstructor
public class OpinionRepository implements OpinionDAO {

    private final OpinionJpaRepository opinionJpaRepository;
    private final OpinionMapper opinionMapper;

    @Override
    public List<OpinionDTO> findAll() {
        return opinionJpaRepository.findAll().stream()
                .map(opinionMapper::mapFromEntity)
                .toList();
    }

    @Override
    public OpinionDTO findById(Integer opinionId) {
        return opinionJpaRepository.findById(opinionId)
                .map(opinionMapper::mapFromEntity)
                .orElseThrow(() -> new NotFoundException("Could not found opinion with id: [%s]".formatted(opinionId)));
    }

    @Override
    public OpinionEntity findEntityById(Integer opinionId) {
        return opinionJpaRepository.findById(opinionId)
                .orElseThrow(() -> new NotFoundException("Could not found opinion with id: [%s]".formatted(opinionId)));
    }

    @Override
    public OpinionEntity saveAndReturn(OpinionEntity opinionEntity) {
        return opinionJpaRepository.save(opinionEntity);
    }

    @Override
    public void save(OpinionEntity opinionEntity) {
        opinionJpaRepository.save(opinionEntity);
    }

    @Override
    public void delete(OpinionEntity opinionEntity) {
        opinionJpaRepository.delete(opinionEntity);
    }

    @Override
    public List<OpinionDTO> findAllByDoctor(Integer doctorId) {
        return  opinionJpaRepository.findAll().stream()
                .filter(opinion -> opinion.getDoctorId().equals(doctorId))
                .map(opinionMapper::mapFromEntity)
                .toList();
    }

    @Override
    public List<OpinionDTO> findAllByPatient(Integer patientId) {
        return  opinionJpaRepository.findAll().stream()
                .filter(opinion -> opinion.getPatientId().equals(patientId))
                .map(opinionMapper::mapFromEntity)
                .toList();
    }
}
