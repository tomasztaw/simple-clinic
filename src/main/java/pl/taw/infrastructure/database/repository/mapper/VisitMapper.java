package pl.taw.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.taw.api.dto.VisitDTO;
import pl.taw.domain.Visit;
import pl.taw.infrastructure.database.entity.VisitEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VisitMapper {

    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    Visit mapFromEntity(VisitEntity entity);

    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    VisitEntity mapToEntity(Visit visit);

//    @Mapping(target = "doctor", ignore = true)
//    @Mapping(target = "patient", ignore = true)
//    VisitDTO mapFromEntityToDTO(VisitEntity visitEntity);

//    @Mapping(target = "doctor", ignore = true)
//    @Mapping(target = "patient", ignore = true)
//    VisitEntity mapFromDtoToEntity(VisitDTO visitDTO);

    @Mapping(source = "doctor.doctorId", target = "doctorId")
    @Mapping(source = "patient.patientId", target = "patientId")
    VisitDTO mapFromEntityToDTO(VisitEntity visitEntity);

    @Mapping(target = "doctor.doctorId", source = "doctorId")
    @Mapping(target = "patient.patientId", source = "patientId")
    VisitEntity mapFromDtoToEntity(VisitDTO visitDTO);

}
