package pl.taw.infrastructure.database.repository.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.OpinionEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-08-27T13:41:07+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.7 (GraalVM Community)"
)
@Component
public class VisitMapperImpl implements VisitMapper {

    @Override
    public VisitDTO mapFromEntity(VisitEntity visitEntity) {
        if ( visitEntity == null ) {
            return null;
        }

        VisitDTO.VisitDTOBuilder visitDTO = VisitDTO.builder();

        visitDTO.visitId( visitEntity.getVisitId() );
        visitDTO.doctorId( visitEntity.getDoctorId() );
        visitDTO.doctor( doctorEntityToDoctorDTO( visitEntity.getDoctor() ) );
        visitDTO.patientId( visitEntity.getPatientId() );
        visitDTO.patient( patientEntityToPatientDTO( visitEntity.getPatient() ) );
        visitDTO.dateTime( visitEntity.getDateTime() );
        visitDTO.note( visitEntity.getNote() );
        visitDTO.status( visitEntity.getStatus() );
        visitDTO.opinion( opinionEntityToOpinionDTO( visitEntity.getOpinion() ) );

        return visitDTO.build();
    }

    @Override
    public VisitEntity mapToEntity(VisitDTO visitDTO) {
        if ( visitDTO == null ) {
            return null;
        }

        VisitEntity.VisitEntityBuilder visitEntity = VisitEntity.builder();

        visitEntity.visitId( visitDTO.getVisitId() );
        visitEntity.doctorId( visitDTO.getDoctorId() );
        visitEntity.doctor( doctorDTOToDoctorEntity( visitDTO.getDoctor() ) );
        visitEntity.patientId( visitDTO.getPatientId() );
        visitEntity.patient( patientDTOToPatientEntity( visitDTO.getPatient() ) );
        visitEntity.note( visitDTO.getNote() );
        visitEntity.dateTime( visitDTO.getDateTime() );
        visitEntity.status( visitDTO.getStatus() );
        visitEntity.opinion( opinionDTOToOpinionEntity( visitDTO.getOpinion() ) );

        return visitEntity.build();
    }

    protected List<VisitEntity> visitDTOListToVisitEntityList(List<VisitDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<VisitEntity> list1 = new ArrayList<VisitEntity>( list.size() );
        for ( VisitDTO visitDTO : list ) {
            list1.add( mapToEntity( visitDTO ) );
        }

        return list1;
    }

    protected DoctorEntity doctorDTOToDoctorEntity(DoctorDTO doctorDTO) {
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
        doctorEntity.visits( visitDTOListToVisitEntityList( doctorDTO.getVisits() ) );

        return doctorEntity.build();
    }

    protected PatientEntity patientDTOToPatientEntity(PatientDTO patientDTO) {
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
        patientEntity.visits( visitDTOListToVisitEntityList( patientDTO.getVisits() ) );

        return patientEntity.build();
    }

    protected OpinionEntity opinionDTOToOpinionEntity(OpinionDTO opinionDTO) {
        if ( opinionDTO == null ) {
            return null;
        }

        OpinionEntity.OpinionEntityBuilder opinionEntity = OpinionEntity.builder();

        opinionEntity.opinionId( opinionDTO.getOpinionId() );
        opinionEntity.doctorId( opinionDTO.getDoctorId() );
        opinionEntity.patientId( opinionDTO.getPatientId() );
        opinionEntity.visitId( opinionDTO.getVisitId() );
        opinionEntity.comment( opinionDTO.getComment() );
        opinionEntity.createdAt( opinionDTO.getCreatedAt() );
        opinionEntity.doctor( doctorDTOToDoctorEntity( opinionDTO.getDoctor() ) );
        opinionEntity.patient( patientDTOToPatientEntity( opinionDTO.getPatient() ) );
        opinionEntity.visit( mapToEntity( opinionDTO.getVisit() ) );

        return opinionEntity.build();
    }
}
