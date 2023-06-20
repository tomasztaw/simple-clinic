package pl.taw.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pl.taw.api.dto.AlterVisitDTO;
import pl.taw.infrastructure.database.entity.AlterVisitEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlterVisitMapper {

    AlterVisitDTO mapFromEntity(AlterVisitEntity alterVisitEntity);

    AlterVisitEntity mapToEntity(AlterVisitDTO alterVisitDTO);

}
