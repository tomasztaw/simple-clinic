package pl.taw.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.domain.Doctor;
import pl.taw.domain.Patient;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PatientMapper {

    Patient mapFromEntity(PatientEntity patientEntity);

    PatientEntity mapToEntity(Patient patient);

    PatientDTO mapFromEntityToDTO(PatientEntity patientEntity);

    PatientEntity mapFromDtoToEntity(PatientDTO patientDTO);

}
