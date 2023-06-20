package pl.taw.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DoctorMapper {

    DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);

    @Mapping(target = "visits", ignore = true)
    DoctorDTO mapFromEntity(DoctorEntity doctorEntity);

    @Mapping(target = "visits", ignore = true)
    DoctorEntity mapToEntity(DoctorDTO doctorDTO);

}
