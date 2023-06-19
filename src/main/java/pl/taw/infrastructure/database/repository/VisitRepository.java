package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pl.taw.api.dto.AlterVisitDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.AlterVisitDAO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.infrastructure.database.repository.jpa.VisitJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.AlterVisitMapper;
import pl.taw.infrastructure.database.repository.mapper.VisitMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@AllArgsConstructor
public class VisitRepository implements VisitDAO {

    private final VisitJpaRepository visitJpaRepository;
    private final VisitMapper visitMapper;


    @Override
    public List<VisitEntity> findAll() {
        log.debug("Fetching all visits from the database");
        return visitJpaRepository.findAll().stream()
                .toList();
    }

    public List<VisitDTO> findAllDto() {
        return visitJpaRepository.findAll().stream()
                .map(visitMapper::mapFromEntityToDTO)
                .toList();
    }

    @Override
    public Optional<VisitDTO> findById(Integer visitId) {
        return visitJpaRepository.findById(visitId)
                .map(visitMapper::mapFromEntityToDTO);
    }

    @Override
    public Optional<VisitEntity> findEntityById(Integer visitId) {
        return visitJpaRepository.findById(visitId);
    }

    @Override
    public VisitEntity save(VisitEntity visitEntity) {
        return visitJpaRepository.save(visitEntity);
    }

    @Override
    public void saveEntity(VisitEntity visitEntity) {
        visitJpaRepository.save(visitEntity);
    }

    @Override
    public void delete(VisitEntity visitEntity) {
        visitJpaRepository.delete(visitEntity);
    }

}
