package pl.taw.infrastructure.database.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.taw.infrastructure.database.entity.PatientEntity;

@Repository
public interface PatientJpaRepository extends JpaRepository<PatientEntity, Integer> {

    boolean existsByPesel(String pesel);

    boolean existsByEmail(String email);

}
