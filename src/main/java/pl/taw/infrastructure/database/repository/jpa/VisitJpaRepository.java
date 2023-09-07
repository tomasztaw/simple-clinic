package pl.taw.infrastructure.database.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import pl.taw.infrastructure.database.entity.VisitEntity;

@Repository
public interface VisitJpaRepository extends JpaRepository<VisitEntity, Integer> {

    @Nullable
    Page<VisitEntity> findAll(@Nullable Pageable pageable);

}
