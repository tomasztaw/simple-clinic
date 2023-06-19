package pl.taw.business.dao;

import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.entity.VisitEntity;

import java.util.List;
import java.util.Optional;

public interface VisitDAO {

    List<VisitEntity> findAll();

    Optional<VisitDTO> findById(Integer visitId);

    Optional<VisitEntity> findEntityById(Integer visitId);

    VisitEntity save(VisitEntity visitEntity);

    void saveEntity(VisitEntity visitEntity);

    void delete(VisitEntity visitEntity);
}
