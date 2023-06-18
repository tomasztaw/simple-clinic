package pl.taw.infrastructure.database.repository.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CarServiceRequestJpaRepository {//extends JpaRepository<CarServiceRequestEntity, Integer> {

//    @Query("""
//        SELECT csr FROM CarServiceRequestEntity csr
//        WHERE csr.completedDateTime IS NULL
//        AND csr.car.vin = :vin
//        """)
//    Set<CarServiceRequestEntity> findActiveServiceRequestsByCarVin(final @Param("vin") String carVin);
//
//    @EntityGraph(
//        type = EntityGraph.EntityGraphType.FETCH,
//        attributePaths = {
//            "car"
//        }
//    )
//    Set<CarServiceRequestEntity> findAllByCompletedDateTimeIsNull();

}
