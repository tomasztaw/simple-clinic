package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.taw.api.dto.PatientDTO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.jpa.PatientJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.PatientMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class PatientRepositoryTest {

    @Mock
    private PatientJpaRepository patientJpaRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientRepository patientRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        // when
        List<PatientEntity> patients = new ArrayList<>();
        patients.add(new PatientEntity());
        patients.add(new PatientEntity());

        Mockito.when(patientJpaRepository.findAll()).thenReturn(patients);

        List<PatientDTO> expectedPatientDTOs = new ArrayList<>();
        expectedPatientDTOs.add(new PatientDTO());
        expectedPatientDTOs.add(new PatientDTO());

        Mockito.when(patientMapper.mapFromEntity(Mockito.any(PatientEntity.class)))
                .thenReturn(new PatientDTO());

        // then
        List<PatientDTO> result = patientRepository.findAll();

        // given
        Assertions.assertEquals(expectedPatientDTOs.size(), result.size());
    }

    @Test
    public void testFindById() {
        // when
        Integer patientId = 1;
        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setPatientId(patientId);

        Mockito.when(patientJpaRepository.findById(patientId)).thenReturn(Optional.of(patientEntity));

        PatientDTO expectedPatientDTO = new PatientDTO();
        expectedPatientDTO.setPatientId(patientId);

        Mockito.when(patientMapper.mapFromEntity(patientEntity)).thenReturn(expectedPatientDTO);

        // then
        PatientDTO result = patientRepository.findById(patientId);

        // given
        Assertions.assertEquals(patientId, result.getPatientId());
    }

    @Test
    public void testFindEntityById() {
        // when
        Integer patientId = 1;
        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setPatientId(patientId);

        Mockito.when(patientJpaRepository.findById(patientId)).thenReturn(Optional.of(patientEntity));

        // then
        PatientEntity result = patientRepository.findEntityById(patientId);

        // given
        Assertions.assertEquals(patientId, result.getPatientId());
    }

    @Test
    public void testFindByPesel() {
        // given
        String pesel = "85061722333";
        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setPesel(pesel);

        Mockito.when(patientJpaRepository.findAll()).thenReturn(Collections.singletonList(patientEntity));

        PatientDTO expectedPatientDTO = new PatientDTO();
        expectedPatientDTO.setPesel(pesel);

        Mockito.when(patientMapper.mapFromEntity(patientEntity)).thenReturn(expectedPatientDTO);

        // when
        PatientDTO result = patientRepository.findByPesel(pesel);

        // then
        Assertions.assertEquals(expectedPatientDTO, result);
        Assertions.assertEquals(pesel.length(), result.getPesel().length());
    }


    @Test
    public void testSaveAndReturn() {
        // when
        PatientEntity patientEntity = new PatientEntity();
        PatientEntity savedPatientEntity = new PatientEntity();
        savedPatientEntity.setPatientId(1);

        Mockito.when(patientJpaRepository.save(patientEntity)).thenReturn(savedPatientEntity);

        // then
        PatientEntity result = patientRepository.saveAndReturn(patientEntity);

        // given
        Assertions.assertEquals(savedPatientEntity.getPatientId(), result.getPatientId());
    }

    @Test
    public void testSave() {
        // given
        PatientEntity patientEntity = new PatientEntity();

        // when
        patientRepository.save(patientEntity);

        // then
        Mockito.verify(patientJpaRepository, Mockito.times(1)).save(patientEntity);
    }

    @Test
    public void testDelete() {
        // given
        PatientEntity patientEntity = new PatientEntity();

        // when
        patientRepository.delete(patientEntity);

        // then
        Mockito.verify(patientJpaRepository, Mockito.times(1)).delete(patientEntity);
    }


}