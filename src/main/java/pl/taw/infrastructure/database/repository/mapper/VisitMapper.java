package pl.taw.infrastructure.database.repository.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VisitMapper {

    VisitMapper INSTANCE = Mappers.getMapper(VisitMapper.class);

    VisitDTO mapFromEntity(VisitEntity visitEntity);

    VisitEntity mapToEntity(VisitDTO visitDTO);


    default DoctorDTO doctorEntityToDoctorDTO(DoctorEntity doctorEntity) {
        if (doctorEntity == null) {
            return null;
        }
        return DoctorDTO.builder()
                .doctorId(doctorEntity.getDoctorId())
                .name(doctorEntity.getName())
                .surname(doctorEntity.getSurname())
                .title(doctorEntity.getTitle())
                .phone(doctorEntity.getPhone())
                .email(doctorEntity.getEmail())
                .build();
    }

    default PatientDTO patientEntityToPatientDTO(PatientEntity patientEntity) {
        if (patientEntity == null) {
            return null;
        }
        return PatientDTO.builder()
                .patientId(patientEntity.getPatientId())
                .name(patientEntity.getName())
                .surname(patientEntity.getSurname())
                .pesel(patientEntity.getPesel())
                .phone(patientEntity.getPhone())
                .email(patientEntity.getEmail())
                .build();
    }

}
