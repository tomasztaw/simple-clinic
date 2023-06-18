package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@AllArgsConstructor
public class PartRepository {//implements PartDAO {

//    private final PartJpaRepository partJpaRepository;
//    private final PartEntityMapper mapper;
//
//    @Override
//    public List<Part> findAll() {
//        return partJpaRepository.findAll().stream()
//            .map(mapper::mapFromEntity)
//            .toList();
//    }
//
//    @Override
//    public Optional<Part> findBySerialNumber(String serialNumber) {
//        return partJpaRepository.findBySerialNumber(serialNumber)
//            .map(mapper::mapFromEntity);
//    }
}
