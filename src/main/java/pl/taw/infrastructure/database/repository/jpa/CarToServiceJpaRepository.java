package pl.taw.infrastructure.database.repository.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarToServiceJpaRepository {//extends JpaRepository<CarToServiceEntity, Integer> {

//    Optional<CarToServiceEntity> findOptionalByVin(String vin);
//
//    @EntityGraph(
//        type = EntityGraph.EntityGraphType.FETCH,
//        attributePaths = {
//            "carServiceRequests",
//            "carServiceRequests.serviceMechanics",
//            "carServiceRequests.serviceMechanics.service",
//            "carServiceRequests.serviceParts",
//            "carServiceRequests.serviceParts.part"
//        }
//    )
//    CarToServiceEntity findByVin(String vin);
}
