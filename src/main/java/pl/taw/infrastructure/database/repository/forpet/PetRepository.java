package pl.taw.infrastructure.database.repository.forpet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.taw.infrastructure.database.entity.PetEntity;

@Repository
public interface PetRepository extends JpaRepository<PetEntity, Integer> {
}
