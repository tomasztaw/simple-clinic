package pl.taw.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pl.taw.infrastructure.database.entity.ServiceMechanicEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceMechanicEntityMapper {

//    ServiceMechanicEntity mapToEntity(ServiceMechanic serviceMechanic);
}
