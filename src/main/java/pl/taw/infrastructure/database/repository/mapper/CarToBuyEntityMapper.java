package pl.taw.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.taw.infrastructure.database.entity.CarToBuyEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarToBuyEntityMapper {

//    @Mapping(target = "invoice", ignore = true)
//    CarToBuy mapFromEntity(CarToBuyEntity entity);
}
