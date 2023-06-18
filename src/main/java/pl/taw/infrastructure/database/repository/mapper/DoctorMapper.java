package pl.taw.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.domain.Doctor;
import pl.taw.infrastructure.database.entity.DoctorEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DoctorMapper {

    Doctor mapFromEntity(DoctorEntity doctorEntity);

    DoctorEntity mapToEntity(Doctor doctor);

    DoctorDTO mapFromEntityToDTO(DoctorEntity doctorEntity);

    DoctorEntity mapFromDtoToEntity(DoctorDTO doctorDTO);

}
