package pl.taw.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.OpinionEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OpinionMapper {

    OpinionMapper INSTANCE = Mappers.getMapper(OpinionMapper.class);

    @Mapping(target = "doctor", source = "doctor")
    @Mapping(target = "patient", source = "patient")
    @Mapping(target = "visit", ignore = true)
    OpinionDTO mapFromEntity(OpinionEntity opinionEntity);

    @Mapping(target = "doctor", source = "doctor")
    @Mapping(target = "patient", source = "patient")
    @Mapping(target = "visit", ignore = true)
    OpinionEntity mapToEntity(OpinionDTO opinionDTO);

    default DoctorDTO mapDoctorWithoutOpinions(DoctorEntity doctor) {
        return DoctorDTO.builder()
                .doctorId(doctor.getDoctorId())
                .name(doctor.getName())
                .surname(doctor.getSurname())
                .title(doctor.getTitle())
                .phone(doctor.getPhone())
                .email(doctor.getEmail())
                .build();
    }

    default PatientDTO mapPatientWithoutOpinions(PatientEntity patient) {
        return PatientDTO.builder()
                .patientId(patient.getPatientId())
                .name(patient.getName())
                .surname(patient.getSurname())
                .pesel(patient.getPesel())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .build();
    }

    default VisitDTO mapVisitWithoutOpinion(VisitEntity visitEntity) {
        return VisitDTO.builder()
                .visitId(visitEntity.getVisitId())
//                .doctor(mapDoctorWithoutOpinions(visitEntity.getDoctor()))
                .doctorId(visitEntity.getDoctorId())
//                .patient(mapPatientWithoutOpinions(visitEntity.getPatient()))
                .patientId(visitEntity.getPatientId())
                .note(visitEntity.getNote())
                .dateTime(visitEntity.getDateTime())
                .status(visitEntity.getStatus())
                .build();
    }
}


