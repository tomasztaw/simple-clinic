package pl.taw.infrastructure.database.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.taw.infrastructure.database.entity.AlterVisitEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;

@Repository
public interface AlterVisitJpaRepository extends JpaRepository<AlterVisitEntity, Integer> {

}
