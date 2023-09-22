package pl.taw.infrastructure.database.repository.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pl.taw.api.dto.PatientDTO;
import pl.taw.infrastructure.database.entity.PatientEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-09-22T11:20:33+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.7 (GraalVM Community)"
)
@Component
public class PatientMapperImpl implements PatientMapper {

    @Override
    public PatientDTO mapFromEntity(PatientEntity patientEntity) {
        if ( patientEntity == null ) {
            return null;
        }

        PatientDTO.PatientDTOBuilder patientDTO = PatientDTO.builder();

        patientDTO.patientId( patientEntity.getPatientId() );
        patientDTO.name( patientEntity.getName() );
        patientDTO.surname( patientEntity.getSurname() );
        patientDTO.pesel( patientEntity.getPesel() );
        patientDTO.phone( patientEntity.getPhone() );
        patientDTO.email( patientEntity.getEmail() );

        return patientDTO.build();
    }

    @Override
    public PatientEntity mapToEntity(PatientDTO patientDTO) {
        if ( patientDTO == null ) {
            return null;
        }

        PatientEntity.PatientEntityBuilder patientEntity = PatientEntity.builder();

        patientEntity.patientId( patientDTO.getPatientId() );
        patientEntity.name( patientDTO.getName() );
        patientEntity.surname( patientDTO.getSurname() );
        patientEntity.pesel( patientDTO.getPesel() );
        patientEntity.phone( patientDTO.getPhone() );
        patientEntity.email( patientDTO.getEmail() );

        return patientEntity.build();
    }
}
