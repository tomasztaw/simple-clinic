package pl.taw.business.dao;

import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.entity.VisitEntity;

import java.util.List;

public interface VisitDAO {

    List<VisitDTO> findAll();

    VisitDTO findById(Integer visitId);

    VisitEntity findEntityById(Integer visitId);

    VisitEntity saveAndReturn(VisitEntity visitEntity);

    void save(VisitEntity visitEntity);

    void delete(VisitEntity visitEntity);
}
