package pl.taw.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pl.taw.api.dto.DoctorScheduleDTO;
import pl.taw.infrastructure.database.entity.DoctorScheduleEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DoctorScheduleMapper {

    DoctorScheduleDTO mapFromEntity(DoctorScheduleEntity doctorScheduleEntity);

    DoctorScheduleEntity mapToEntity(DoctorScheduleDTO doctorScheduleDTO);

}
