package pl.taw.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.taw.api.dto.PatientDTO;
import pl.taw.infrastructure.database.entity.PatientEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PatientMapper {

    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    @Mapping(target = "visits", ignore = true)
    PatientDTO mapFromEntity(PatientEntity patientEntity);

    @Mapping(target = "visits", ignore = true)
    PatientEntity mapToEntity(PatientDTO patientDTO);


}
