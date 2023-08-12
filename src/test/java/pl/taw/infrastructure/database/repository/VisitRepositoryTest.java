package pl.taw.infrastructure.database.repository;

import io.swagger.annotations.Example;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.infrastructure.database.repository.jpa.VisitJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.VisitMapper;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VisitRepositoryTest {

    @Mock
    private VisitJpaRepository visitJpaRepository;

    @Mock
    private VisitMapper visitMapper;

    @InjectMocks
    private VisitRepository visitRepository;


    @Test
    public void testFindAll() {
        // when
        VisitEntity visitEntity1 = EntityFixtures.someVisit1();
        VisitEntity visitEntity2 = EntityFixtures.someVisit2();
        List<VisitEntity> visitEntities = List.of(visitEntity1, visitEntity2);

        VisitDTO visitDTO1 = DtoFixtures.someVisit1();
        VisitDTO visitDTO2 = DtoFixtures.someVisit2();
        List<VisitDTO> expectedVisitDTOs = List.of(visitDTO1, visitDTO2);

        when(visitJpaRepository.findAll()).thenReturn(visitEntities);
        when(visitMapper.mapFromEntity(visitEntity1)).thenReturn(visitDTO1);
        when(visitMapper.mapFromEntity(visitEntity2)).thenReturn(visitDTO2);

        // then
        List<VisitDTO> result = visitRepository.findAll();

        // given
        Assertions.assertEquals(expectedVisitDTOs.size(), result.size());
        Assertions.assertEquals(expectedVisitDTOs.get(0), result.get(0));

        verify(visitJpaRepository, times(1)).findAll();
        verify(visitMapper, times(2)).mapFromEntity(ArgumentMatchers.any(VisitEntity.class));
        verifyNoMoreInteractions(visitJpaRepository, visitMapper);
    }

    @Test
    public void testFindById() {
        // given
        Integer visitId = 4;
        VisitEntity visitEntity = EntityFixtures.someVisit4();
        VisitDTO expectedVisitDTO = DtoFixtures.someVisit4().withVisitId(visitId);

        when(visitJpaRepository.findById(visitId)).thenReturn(Optional.of(visitEntity));
        when(visitMapper.mapFromEntity(visitEntity)).thenReturn(expectedVisitDTO);

        // when
        VisitDTO result = visitRepository.findById(visitId);

        // then
        Assertions.assertEquals(visitId, result.getVisitId());

        verify(visitJpaRepository, times(1)).findById(visitId);
        verify(visitMapper, times(1)).mapFromEntity(visitEntity);
        verifyNoMoreInteractions(visitJpaRepository, visitMapper);
    }

    @Test
    public void testFindEntityById() {
        // given
        Integer visitId = 5;
        VisitEntity visitEntity = EntityFixtures.someVisit5();

        when(visitJpaRepository.findById(visitId)).thenReturn(Optional.of(visitEntity));

        // when
        VisitEntity result = visitRepository.findEntityById(visitId);

        // then
        Assertions.assertEquals(visitId, result.getVisitId());

        verify(visitJpaRepository, times(1)).findById(visitId);
        verifyNoMoreInteractions(visitJpaRepository);
    }

    @Test
    public void testSaveAndReturn() {
        // given
        VisitEntity visitEntity = EntityFixtures.someVisit1();
        VisitEntity savedVisitEntity = EntityFixtures.someVisit1().withPatientId(visitEntity.getPatient().getPatientId());

        when(visitJpaRepository.saveAndFlush(visitEntity)).thenReturn(savedVisitEntity);

        // when
        VisitEntity result = visitRepository.saveAndReturn(visitEntity);

        // then
        Assertions.assertEquals(savedVisitEntity.getVisitId(), result.getVisitId());

        verify(visitJpaRepository, times(1)).saveAndFlush(visitEntity);
        verifyNoMoreInteractions(visitJpaRepository);
    }

    @Test
    public void testSave() {
        // given
        VisitEntity visitEntity = EntityFixtures.someVisit3();

        // when
        visitRepository.save(visitEntity);

        // then
        verify(visitJpaRepository, times(1)).save(visitEntity);
        verifyNoMoreInteractions(visitJpaRepository);
    }

    @Test
    public void testDelete() {
        // given
        VisitEntity visitEntity = EntityFixtures.someVisit5();

        // when
        visitRepository.delete(visitEntity);

        // then
        verify(visitJpaRepository, times(1)).delete(visitEntity);
        verifyNoMoreInteractions(visitJpaRepository);
    }


    @Test
    public void testFindAllByDoctor() {
        // given
        Integer doctorId = 1;
        DoctorEntity doctorEntity = EntityFixtures.someDoctor1();
        VisitEntity visitEntity1 = EntityFixtures.someVisit1();
        VisitEntity visitEntity2 = EntityFixtures.someVisit2().withDoctor(doctorEntity);
        List<VisitEntity> visitEntities = List.of(visitEntity1, visitEntity2);

        VisitDTO visitDTO1 = DtoFixtures.someVisit1().withDoctorId(doctorId);
        VisitDTO visitDTO2 = DtoFixtures.someVisit2();

        when(visitJpaRepository.findAll()).thenReturn(visitEntities);
        when(visitMapper.mapFromEntity(visitEntity1)).thenReturn(visitDTO1);
        when(visitMapper.mapFromEntity(visitEntity2)).thenReturn(visitDTO2);

        // when
        List<VisitDTO> result = visitRepository.findAllByDoctor(doctorId);

        // then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(visitDTO1, result.get(0));
        Assertions.assertEquals(visitDTO2, result.get(1));
        Assertions.assertTrue(result.stream().allMatch(visit -> visit.getDoctorId().equals(doctorId)));

        verify(visitJpaRepository, times(1)).findAll();
        verify(visitMapper, times(2)).mapFromEntity(ArgumentMatchers.any(VisitEntity.class));
        verifyNoMoreInteractions(visitJpaRepository, visitMapper);
    }

    @Test
    public void testFindAllByPatient() {
        // given
        Integer patientId = 1;
        PatientEntity patientEntity = EntityFixtures.somePatient1();
        VisitEntity visitEntity1 = EntityFixtures.someVisit1();
        VisitEntity visitEntity2 = EntityFixtures.someVisit2().withPatient(patientEntity);
        List<VisitEntity> visitEntities = List.of(visitEntity1, visitEntity2);

        VisitDTO visitDTO1 = DtoFixtures.someVisit1().withPatientId(patientId);
        VisitDTO visitDTO2 = DtoFixtures.someVisit2().withPatientId(patientId);

        when(visitJpaRepository.findAll()).thenReturn(visitEntities);
        when(visitMapper.mapFromEntity(visitEntity1)).thenReturn(visitDTO1);
        when(visitMapper.mapFromEntity(visitEntity2)).thenReturn(visitDTO2);

        // when
        List<VisitDTO> result = visitRepository.findAllByPatient(patientId);

        // then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(visitDTO1, result.get(0));
        Assertions.assertEquals(visitDTO2, result.get(1));
        Assertions.assertTrue(result.stream().allMatch(visit -> visit.getPatientId().equals(patientId)));

        verify(visitJpaRepository, times(1)).findAll();
        verify(visitMapper, times(2)).mapFromEntity(ArgumentMatchers.any(VisitEntity.class));
        verifyNoMoreInteractions(visitJpaRepository, visitMapper);
    }

}
