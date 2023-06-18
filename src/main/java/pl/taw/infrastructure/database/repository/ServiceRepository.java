package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@AllArgsConstructor
public class ServiceRepository {// implements ServiceDAO {

//    private final ServiceJpaRepository serviceJpaRepository;
//    private final ServiceEntityMapper mapper;
//
//    @Override
//    public List<Service> findAll() {
//        return serviceJpaRepository.findAll().stream()
//            .map(mapper::mapFromEntity)
//            .toList();
//    }
//
//    @Override
//    public Optional<Service> findByServiceCode(String serviceCode) {
//        return serviceJpaRepository.findByServiceCode(serviceCode)
//            .map(mapper::mapFromEntity);
//    }
}
