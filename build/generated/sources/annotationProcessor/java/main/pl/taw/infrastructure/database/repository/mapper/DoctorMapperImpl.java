package pl.taw.infrastructure.database.repository.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-08-27T13:41:07+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.7 (GraalVM Community)"
)
@Component
public class DoctorMapperImpl implements DoctorMapper {

    @Override
    public DoctorDTO mapFromEntity(DoctorEntity doctorEntity) {
        if ( doctorEntity == null ) {
            return null;
        }

        DoctorDTO.DoctorDTOBuilder doctorDTO = DoctorDTO.builder();

        doctorDTO.doctorId( doctorEntity.getDoctorId() );
        doctorDTO.name( doctorEntity.getName() );
        doctorDTO.surname( doctorEntity.getSurname() );
        doctorDTO.title( doctorEntity.getTitle() );
        doctorDTO.phone( doctorEntity.getPhone() );
        doctorDTO.email( doctorEntity.getEmail() );

        return doctorDTO.build();
    }

    @Override
    public DoctorEntity mapToEntity(DoctorDTO doctorDTO) {
        if ( doctorDTO == null ) {
            return null;
        }

        DoctorEntity.DoctorEntityBuilder doctorEntity = DoctorEntity.builder();

        doctorEntity.doctorId( doctorDTO.getDoctorId() );
        doctorEntity.name( doctorDTO.getName() );
        doctorEntity.surname( doctorDTO.getSurname() );
        doctorEntity.title( doctorDTO.getTitle() );
        doctorEntity.phone( doctorDTO.getPhone() );
        doctorEntity.email( doctorDTO.getEmail() );

        return doctorEntity.build();
    }
}
