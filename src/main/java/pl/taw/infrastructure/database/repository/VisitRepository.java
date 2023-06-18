package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.infrastructure.database.repository.jpa.VisitJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.VisitMapper;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class VisitRepository implements VisitDAO {

    private final VisitJpaRepository visitJpaRepository;
    private final VisitMapper visitMapper;


    @Override
    public List<VisitDTO> findAll() {
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
    public VisitDTO save(VisitDTO visitDTO) {
        VisitEntity visitEntity = visitMapper.mapFromDtoToEntity(visitDTO);
        VisitEntity savedVisitEntity = visitJpaRepository.save(visitEntity);
        return visitMapper.mapFromEntityToDTO(savedVisitEntity);
    }

    @Override
    public void delete(VisitDTO visitDTO) {
        VisitEntity visitEntity = visitMapper.mapFromDtoToEntity(visitDTO);
        visitJpaRepository.delete(visitEntity);
    }

}
