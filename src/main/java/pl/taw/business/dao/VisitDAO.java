package pl.taw.business.dao;

import pl.taw.api.dto.VisitDTO;

import java.util.List;
import java.util.Optional;

public interface VisitDAO {

    List<VisitDTO> findAll();

    Optional<VisitDTO> findById(Integer visitId);

    VisitDTO save(VisitDTO visitDTO);

    void delete(VisitDTO visitDTO);
}
