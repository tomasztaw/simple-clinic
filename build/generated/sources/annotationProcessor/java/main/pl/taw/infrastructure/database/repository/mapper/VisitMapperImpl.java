package pl.taw.infrastructure.database.repository.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pl.taw.api.dto.VisitDTO;
import pl.taw.domain.Visit;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-19T21:09:37+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.7 (GraalVM Community)"
)
@Component
public class VisitMapperImpl implements VisitMapper {

    @Override
    public Visit mapFromEntity(VisitEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Visit.VisitBuilder visit = Visit.builder();

        visit.visitId( entity.getVisitId() );
        visit.dateTime( entity.getDateTime() );
        visit.note( entity.getNote() );
        visit.status( entity.getStatus() );

        return visit.build();
    }

    @Override
    public VisitEntity mapToEntity(Visit visit) {
        if ( visit == null ) {
            return null;
        }

        VisitEntity.VisitEntityBuilder visitEntity = VisitEntity.builder();

        visitEntity.visitId( visit.getVisitId() );
        visitEntity.note( visit.getNote() );
        visitEntity.dateTime( visit.getDateTime() );
        visitEntity.status( visit.getStatus() );

        return visitEntity.build();
    }

    @Override
    public VisitDTO mapFromEntityToDTO(VisitEntity visitEntity) {
        if ( visitEntity == null ) {
            return null;
        }

        VisitDTO.VisitDTOBuilder visitDTO = VisitDTO.builder();

        visitDTO.doctorId( visitEntityDoctorDoctorId( visitEntity ) );
        visitDTO.patientId( visitEntityPatientPatientId( visitEntity ) );
        visitDTO.visitId( visitEntity.getVisitId() );
        visitDTO.dateTime( visitEntity.getDateTime() );
        visitDTO.note( visitEntity.getNote() );
        visitDTO.status( visitEntity.getStatus() );

        return visitDTO.build();
    }

    @Override
    public VisitEntity mapFromDtoToEntity(VisitDTO visitDTO) {
        if ( visitDTO == null ) {
            return null;
        }

        VisitEntity.VisitEntityBuilder visitEntity = VisitEntity.builder();

        visitEntity.doctor( visitDTOToDoctorEntity( visitDTO ) );
        visitEntity.patient( visitDTOToPatientEntity( visitDTO ) );
        visitEntity.visitId( visitDTO.getVisitId() );
        visitEntity.doctorId( visitDTO.getDoctorId() );
        visitEntity.patientId( visitDTO.getPatientId() );
        visitEntity.note( visitDTO.getNote() );
        visitEntity.dateTime( visitDTO.getDateTime() );
        visitEntity.status( visitDTO.getStatus() );

        return visitEntity.build();
    }

    private Integer visitEntityDoctorDoctorId(VisitEntity visitEntity) {
        if ( visitEntity == null ) {
            return null;
        }
        DoctorEntity doctor = visitEntity.getDoctor();
        if ( doctor == null ) {
            return null;
        }
        Integer doctorId = doctor.getDoctorId();
        if ( doctorId == null ) {
            return null;
        }
        return doctorId;
    }

    private Integer visitEntityPatientPatientId(VisitEntity visitEntity) {
        if ( visitEntity == null ) {
            return null;
        }
        PatientEntity patient = visitEntity.getPatient();
        if ( patient == null ) {
            return null;
        }
        Integer patientId = patient.getPatientId();
        if ( patientId == null ) {
            return null;
        }
        return patientId;
    }

    protected DoctorEntity visitDTOToDoctorEntity(VisitDTO visitDTO) {
        if ( visitDTO == null ) {
            return null;
        }

        DoctorEntity.DoctorEntityBuilder doctorEntity = DoctorEntity.builder();

        doctorEntity.doctorId( visitDTO.getDoctorId() );

        return doctorEntity.build();
    }

    protected PatientEntity visitDTOToPatientEntity(VisitDTO visitDTO) {
        if ( visitDTO == null ) {
            return null;
        }

        PatientEntity.PatientEntityBuilder patientEntity = PatientEntity.builder();

        patientEntity.patientId( visitDTO.getPatientId() );

        return patientEntity.build();
    }
}
