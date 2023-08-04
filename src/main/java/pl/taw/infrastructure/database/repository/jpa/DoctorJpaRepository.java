package pl.taw.infrastructure.database.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.taw.infrastructure.database.entity.DoctorEntity;

@Repository
public interface DoctorJpaRepository extends JpaRepository<DoctorEntity, Integer> {

}
