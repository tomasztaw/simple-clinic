package pl.taw.infrastructure.database.repository.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.domain.Doctor;
import pl.taw.domain.Patient;
import pl.taw.domain.Visit;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-19T21:09:38+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.7 (GraalVM Community)"
)
@Component
public class DoctorMapperImpl implements DoctorMapper {

    @Override
    public Doctor mapFromEntity(DoctorEntity doctorEntity) {
        if ( doctorEntity == null ) {
            return null;
        }

        Doctor.DoctorBuilder doctor = Doctor.builder();

        doctor.doctorId( doctorEntity.getDoctorId() );
        doctor.name( doctorEntity.getName() );
        doctor.surname( doctorEntity.getSurname() );
        doctor.title( doctorEntity.getTitle() );
        doctor.phone( doctorEntity.getPhone() );
        doctor.email( doctorEntity.getEmail() );
        doctor.visits( visitEntityListToVisitList( doctorEntity.getVisits() ) );

        return doctor.build();
    }

    @Override
    public DoctorEntity mapToEntity(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }

        DoctorEntity.DoctorEntityBuilder doctorEntity = DoctorEntity.builder();

        doctorEntity.doctorId( doctor.getDoctorId() );
        doctorEntity.name( doctor.getName() );
        doctorEntity.surname( doctor.getSurname() );
        doctorEntity.title( doctor.getTitle() );
        doctorEntity.phone( doctor.getPhone() );
        doctorEntity.email( doctor.getEmail() );
        doctorEntity.visits( visitListToVisitEntityList( doctor.getVisits() ) );

        return doctorEntity.build();
    }

    @Override
    public DoctorDTO mapFromEntityToDTO(DoctorEntity doctorEntity) {
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
        doctorDTO.visits( visitEntityListToVisitDTOList( doctorEntity.getVisits() ) );

        return doctorDTO.build();
    }

    @Override
    public DoctorEntity mapFromDtoToEntity(DoctorDTO doctorDTO) {
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

    protected List<Visit> visitEntityListToVisitList(List<VisitEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<Visit> list1 = new ArrayList<Visit>( list.size() );
        for ( VisitEntity visitEntity : list ) {
            list1.add( visitEntityToVisit( visitEntity ) );
        }

        return list1;
    }

    protected Patient patientEntityToPatient(PatientEntity patientEntity) {
        if ( patientEntity == null ) {
            return null;
        }

        Patient.PatientBuilder patient = Patient.builder();

        patient.patientId( patientEntity.getPatientId() );
        patient.name( patientEntity.getName() );
        patient.surname( patientEntity.getSurname() );
        patient.pesel( patientEntity.getPesel() );
        patient.phone( patientEntity.getPhone() );
        patient.email( patientEntity.getEmail() );
        patient.visits( visitEntityListToVisitList( patientEntity.getVisits() ) );

        return patient.build();
    }

    protected Visit visitEntityToVisit(VisitEntity visitEntity) {
        if ( visitEntity == null ) {
            return null;
        }

        Visit.VisitBuilder visit = Visit.builder();

        visit.visitId( visitEntity.getVisitId() );
        visit.doctor( mapFromEntity( visitEntity.getDoctor() ) );
        visit.patient( patientEntityToPatient( visitEntity.getPatient() ) );
        visit.dateTime( visitEntity.getDateTime() );
        visit.note( visitEntity.getNote() );
        visit.status( visitEntity.getStatus() );

        return visit.build();
    }

    protected List<VisitEntity> visitListToVisitEntityList(List<Visit> list) {
        if ( list == null ) {
            return null;
        }

        List<VisitEntity> list1 = new ArrayList<VisitEntity>( list.size() );
        for ( Visit visit : list ) {
            list1.add( visitToVisitEntity( visit ) );
        }

        return list1;
    }

    protected PatientEntity patientToPatientEntity(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        PatientEntity.PatientEntityBuilder patientEntity = PatientEntity.builder();

        patientEntity.patientId( patient.getPatientId() );
        patientEntity.name( patient.getName() );
        patientEntity.surname( patient.getSurname() );
        patientEntity.pesel( patient.getPesel() );
        patientEntity.phone( patient.getPhone() );
        patientEntity.email( patient.getEmail() );
        patientEntity.visits( visitListToVisitEntityList( patient.getVisits() ) );

        return patientEntity.build();
    }

    protected VisitEntity visitToVisitEntity(Visit visit) {
        if ( visit == null ) {
            return null;
        }

        VisitEntity.VisitEntityBuilder visitEntity = VisitEntity.builder();

        visitEntity.visitId( visit.getVisitId() );
        visitEntity.note( visit.getNote() );
        visitEntity.dateTime( visit.getDateTime() );
        visitEntity.status( visit.getStatus() );
        visitEntity.doctor( mapToEntity( visit.getDoctor() ) );
        visitEntity.patient( patientToPatientEntity( visit.getPatient() ) );

        return visitEntity.build();
    }

    protected VisitDTO visitEntityToVisitDTO(VisitEntity visitEntity) {
        if ( visitEntity == null ) {
            return null;
        }

        VisitDTO.VisitDTOBuilder visitDTO = VisitDTO.builder();

        visitDTO.visitId( visitEntity.getVisitId() );
        visitDTO.doctorId( visitEntity.getDoctorId() );
        visitDTO.patientId( visitEntity.getPatientId() );
        visitDTO.dateTime( visitEntity.getDateTime() );
        visitDTO.note( visitEntity.getNote() );
        visitDTO.status( visitEntity.getStatus() );

        return visitDTO.build();
    }

    protected List<VisitDTO> visitEntityListToVisitDTOList(List<VisitEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<VisitDTO> list1 = new ArrayList<VisitDTO>( list.size() );
        for ( VisitEntity visitEntity : list ) {
            list1.add( visitEntityToVisitDTO( visitEntity ) );
        }

        return list1;
    }

    protected VisitEntity visitDTOToVisitEntity(VisitDTO visitDTO) {
        if ( visitDTO == null ) {
            return null;
        }

        VisitEntity.VisitEntityBuilder visitEntity = VisitEntity.builder();

        visitEntity.visitId( visitDTO.getVisitId() );
        visitEntity.doctorId( visitDTO.getDoctorId() );
        visitEntity.patientId( visitDTO.getPatientId() );
        visitEntity.note( visitDTO.getNote() );
        visitEntity.dateTime( visitDTO.getDateTime() );
        visitEntity.status( visitDTO.getStatus() );

        return visitEntity.build();
    }

    protected List<VisitEntity> visitDTOListToVisitEntityList(List<VisitDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<VisitEntity> list1 = new ArrayList<VisitEntity>( list.size() );
        for ( VisitDTO visitDTO : list ) {
            list1.add( visitDTOToVisitEntity( visitDTO ) );
        }

        return list1;
    }
}
