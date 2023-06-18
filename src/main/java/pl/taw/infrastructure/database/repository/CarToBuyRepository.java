package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CarToBuyRepository {//implements CarToBuyDAO {

//    private final CarToBuyJpaRepository carToBuyJpaRepository;
//    private final CarToBuyEntityMapper carToBuyEntityMapper;
//
//    @Override
//    public List<CarToBuy> findAvailable() {
//        return carToBuyJpaRepository.findAvailableCars().stream()
//            .map(carToBuyEntityMapper::mapFromEntity)
//            .toList();
//    }
//
//    @Override
//    public Optional<CarToBuy> findCarToBuyByVin(String vin) {
//        return carToBuyJpaRepository.findByVin(vin)
//            .map(carToBuyEntityMapper::mapFromEntity);
//    }
}
