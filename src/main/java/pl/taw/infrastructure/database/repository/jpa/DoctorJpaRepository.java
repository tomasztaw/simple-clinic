package pl.taw.infrastructure.database.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.taw.infrastructure.database.entity.DoctorEntity;

import java.util.Optional;

@Repository
public interface DoctorJpaRepository extends JpaRepository<DoctorEntity, Integer> {

    @Override
    <S extends DoctorEntity> S saveAndFlush(S entity);

    // dodane na próbę dla testów
    // ###########################
    Optional<DoctorEntity> findByNameAndSurname(String name, String surname);

    void deleteByNameAndSurname(String name, String surname);
    // ###########################

}
