package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.infrastructure.database.repository.jpa.VisitJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.VisitMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VisitRepositoryTest {

    @Mock
    private VisitJpaRepository visitJpaRepository;

    @Mock
    private VisitMapper visitMapper;

    @InjectMocks
    private VisitRepository visitRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        // when
        List<VisitEntity> visitEntities = new ArrayList<>();
        visitEntities.add(new VisitEntity());
        visitEntities.add(new VisitEntity());

        Mockito.when(visitJpaRepository.findAll()).thenReturn(visitEntities);

        List<VisitDTO> expectedVisitDTOs = new ArrayList<>();
        expectedVisitDTOs.add(new VisitDTO());
        expectedVisitDTOs.add(new VisitDTO());

        Mockito.when(visitMapper.mapFromEntity(Mockito.any(VisitEntity.class)))
                .thenReturn(new VisitDTO());

        // then
        List<VisitDTO> result = visitRepository.findAll();

        // given
        Assertions.assertEquals(expectedVisitDTOs.size(), result.size());
    }

    @Test
    public void testFindById() {
        // when
        Integer visitId = 1;
        VisitEntity visitEntity = new VisitEntity();
        visitEntity.setVisitId(visitId);

        Mockito.when(visitJpaRepository.findById(visitId)).thenReturn(Optional.of(visitEntity));

        VisitDTO expectedVisitDTO = new VisitDTO();
        expectedVisitDTO.setVisitId(visitId);

        Mockito.when(visitMapper.mapFromEntity(visitEntity)).thenReturn(expectedVisitDTO);

        // then
        VisitDTO result = visitRepository.findById(visitId);

        // given
        Assertions.assertEquals(visitId, result.getVisitId());
    }

    @Test
    public void testFindEntityById() {
        // when
        Integer visitId = 1;
        VisitEntity visitEntity = new VisitEntity();
        visitEntity.setVisitId(visitId);

        Mockito.when(visitJpaRepository.findById(visitId)).thenReturn(Optional.of(visitEntity));

        // then
        VisitEntity result = visitRepository.findEntityById(visitId);

        // given
        Assertions.assertEquals(visitId, result.getVisitId());
    }

    @Test
    public void testSaveAndReturn() {
        // when
        VisitEntity visitEntity = new VisitEntity();
        VisitEntity savedVisitEntity = new VisitEntity();
        savedVisitEntity.setVisitId(1);

        Mockito.when(visitJpaRepository.save(visitEntity)).thenReturn(savedVisitEntity);

        // then
        VisitEntity result = visitRepository.saveAndReturn(visitEntity);

        // given
        Assertions.assertEquals(savedVisitEntity.getVisitId(), result.getVisitId());
    }

    @Test
    public void testSave() {
        // given
        VisitEntity visitEntity = new VisitEntity();

        // when
        visitRepository.save(visitEntity);

        // then
        Mockito.verify(visitJpaRepository, Mockito.times(1)).save(visitEntity);
    }

    @Test
    public void testDelete() {
        // given
        VisitEntity visitEntity = new VisitEntity();

        // when
        visitRepository.delete(visitEntity);

        // then
        Mockito.verify(visitJpaRepository, Mockito.times(1)).delete(visitEntity);
    }


    @Test
    public void testFindAllByDoctor() {
        // given
        Integer doctorId = 1;
        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setDoctorId(doctorId);

        VisitEntity visitEntity1 = new VisitEntity();
        visitEntity1.setDoctor(doctorEntity);

        VisitEntity visitEntity2 = new VisitEntity();
        visitEntity2.setDoctor(doctorEntity);

        List<VisitEntity> visitEntities = List.of(visitEntity1, visitEntity2);

        Mockito.when(visitJpaRepository.findAll()).thenReturn(visitEntities);

        VisitDTO visitDTO1 = new VisitDTO();
        VisitDTO visitDTO2 = new VisitDTO();

        Mockito.when(visitMapper.mapFromEntity(visitEntity1)).thenReturn(visitDTO1);
        Mockito.when(visitMapper.mapFromEntity(visitEntity2)).thenReturn(visitDTO2);

        // when
        List<VisitDTO> result = visitRepository.findAllByDoctor(doctorId);

        // then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(visitDTO1, result.get(0));
        Assertions.assertEquals(visitDTO2, result.get(1));
    }

    @Test
    public void testFindAllByPatient() {
        // given
        Integer patientId = 1;
        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setPatientId(patientId);

        VisitEntity visitEntity1 = new VisitEntity();
        visitEntity1.setPatient(patientEntity);

        VisitEntity visitEntity2 = new VisitEntity();
        visitEntity2.setPatient(patientEntity);

        List<VisitEntity> visitEntities = List.of(visitEntity1, visitEntity2);

        Mockito.when(visitJpaRepository.findAll()).thenReturn(visitEntities);

        VisitDTO visitDTO1 = new VisitDTO();
        VisitDTO visitDTO2 = new VisitDTO();

        Mockito.when(visitMapper.mapFromEntity(visitEntity1)).thenReturn(visitDTO1);
        Mockito.when(visitMapper.mapFromEntity(visitEntity2)).thenReturn(visitDTO2);

        // when
        List<VisitDTO> result = visitRepository.findAllByPatient(patientId);

        // then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(visitDTO1, result.get(0));
        Assertions.assertEquals(visitDTO2, result.get(1));
    }



}
