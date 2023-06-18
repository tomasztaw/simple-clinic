package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@AllArgsConstructor
public class MechanicRepository {//implements MechanicDAO {

//    private final MechanicJpaRepository mechanicJpaRepository;
//    private final MechanicEntityMapper mechanicEntityMapper;
//
//    @Override
//    public List<Mechanic> findAvailable() {
//        return mechanicJpaRepository.findAll().stream()
//            .map(mechanicEntityMapper::mapFromEntity)
//            .toList();
//    }
//
//    @Override
//    public Optional<Mechanic> findByPesel(String pesel) {
//        return mechanicJpaRepository.findByPesel(pesel)
//            .map(mechanicEntityMapper::mapFromEntity);
//    }
}
