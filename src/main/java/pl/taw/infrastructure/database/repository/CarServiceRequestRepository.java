package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.taw.infrastructure.database.repository.jpa.CarServiceRequestJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.CarServiceRequestEntityMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class CarServiceRequestRepository {//implements CarServiceRequestDAO {

//    private final CarServiceRequestJpaRepository carServiceRequestJpaRepository;
//    private final CarServiceRequestEntityMapper carServiceRequestEntityMapper;
//
//    @Override
//    public List<CarServiceRequest> findAvailable() {
//        return carServiceRequestJpaRepository.findAllByCompletedDateTimeIsNull().stream()
//            .map(carServiceRequestEntityMapper::mapFromEntityWithCar)
//            .toList();
//    }
//
//    @Override
//    public Set<CarServiceRequest> findActiveServiceRequestsByCarVin(String carVin) {
//        return carServiceRequestJpaRepository.findActiveServiceRequestsByCarVin(carVin).stream()
//            .map(carServiceRequestEntityMapper::mapFromEntity)
//            .collect(Collectors.toSet());
//    }
}
