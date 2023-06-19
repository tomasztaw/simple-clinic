package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.taw.api.dto.AlterVisitDTO;
import pl.taw.business.dao.AlterVisitDAO;
import pl.taw.infrastructure.database.repository.jpa.AlterVisitJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.AlterVisitMapper;

import java.util.List;

@Repository
@AllArgsConstructor
public class AlterVisitRepository implements AlterVisitDAO {

    private final AlterVisitJpaRepository alterVisitJpaRepository;
    private final AlterVisitMapper alterVisitMapper;

    @Override
    public List<AlterVisitDTO> findAllAlter() {
        return alterVisitJpaRepository.findAll().stream()
                .map(alterVisitMapper::mapFromEntity)
                .toList();
    }
}
