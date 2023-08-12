package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.PatientDTO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.jpa.PatientJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.PatientMapper;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientRepositoryTest {

    @Mock
    private PatientJpaRepository patientJpaRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientRepository patientRepository;


    @Test
    public void testFindAll() {
        // given
        PatientEntity patientEntity1 = EntityFixtures.somePatient1();
        PatientEntity patientEntity2 = EntityFixtures.somePatient2();
        PatientEntity patientEntity3 = EntityFixtures.somePatient3();
        List<PatientEntity> patients = List.of(patientEntity1, patientEntity2, patientEntity3);
        PatientDTO patientDTO1 = DtoFixtures.somePatient1();
        PatientDTO patientDTO2 = DtoFixtures.somePatient2();
        PatientDTO patientDTO3 = DtoFixtures.somePatient2().withPatientId(3).withName("Jan").withSurname("Wilk")
                .withPesel("8506171818199").withPhone("+48 100 200 888").withEmail("j.wilku@wp.pl");
        List<PatientDTO> expectedPatientDTOs = List.of(patientDTO1, patientDTO2, patientDTO3);

        when(patientJpaRepository.findAll()).thenReturn(patients);

        when(patientMapper.mapFromEntity(any(PatientEntity.class)))
                .thenAnswer(invocation -> {
            PatientEntity entity = invocation.getArgument(0);
            int index = patients.indexOf(entity);
            return expectedPatientDTOs.get(index);
        });

        // when
        List<PatientDTO> result = patientRepository.findAll();

        // then
        assertEquals(expectedPatientDTOs.size(), result.size());

        verify(patientJpaRepository, times(1)).findAll();
        verify(patientMapper, times(patients.size())).mapFromEntity(any(PatientEntity.class));
        verifyNoMoreInteractions(patientJpaRepository, patientMapper);
    }

    @Test
    public void testFindById() {
        // given
        Integer patientId = 2;
        PatientEntity patientEntity = EntityFixtures.somePatient2();
        PatientDTO expectedPatientDTO = DtoFixtures.somePatient2().withPatientId(patientId);

        when(patientJpaRepository.findById(patientId)).thenReturn(Optional.of(patientEntity));
        when(patientMapper.mapFromEntity(patientEntity)).thenReturn(expectedPatientDTO);

        // when
        PatientDTO result = patientRepository.findById(patientId);

        // then
        assertEquals(patientId, result.getPatientId());

        verify(patientJpaRepository, times(1)).findById(patientId);
        verify(patientMapper, times(1)).mapFromEntity(patientEntity);
        verifyNoMoreInteractions(patientJpaRepository, patientMapper);
    }

    @Test
    public void testFindEntityById() {
        // given
        Integer patientId = 5;
        PatientEntity patientEntity = EntityFixtures.somePatient5();

        when(patientJpaRepository.findById(patientId)).thenReturn(Optional.of(patientEntity));

        // when
        PatientEntity result = patientRepository.findEntityById(patientId);

        // then
        assertNotNull(result);
        assertEquals(patientId, result.getPatientId());

        verify(patientJpaRepository, times(1)).findById(patientId);
        verifyNoMoreInteractions(patientJpaRepository);
    }

    @Test
    public void testFindByPesel() {
        // given
        String pesel = "85061722333";
        PatientEntity patientEntity = EntityFixtures.somePatient4().withPesel(pesel);
        PatientDTO expectedPatientDTO = DtoFixtures.somePatient2().withPesel(pesel);

        when(patientJpaRepository.findAll()).thenReturn(Collections.singletonList(patientEntity));
        when(patientMapper.mapFromEntity(patientEntity)).thenReturn(expectedPatientDTO);

        // when
        PatientDTO result = patientRepository.findByPesel(pesel);

        // then
        assertEquals(expectedPatientDTO, result);
        assertEquals(pesel.length(), result.getPesel().length());
        assertSame(pesel, result.getPesel());

        verify(patientJpaRepository, times(1)).findAll();
        verify(patientMapper, times(1)).mapFromEntity(patientEntity);
        verifyNoMoreInteractions(patientJpaRepository, patientMapper);
    }

    @Test
    public void testSaveAndReturn() {
        // given
        PatientEntity patientEntity = EntityFixtures.somePatient1();
        PatientEntity savedPatientEntity = patientEntity.withPatientId(2);

        when(patientJpaRepository.saveAndFlush(patientEntity)).thenReturn(savedPatientEntity);

        // when
        PatientEntity result = patientRepository.saveAndReturn(patientEntity);

        // then
        assertEquals(savedPatientEntity.getPatientId(), result.getPatientId());

        verify(patientJpaRepository, times(1)).saveAndFlush(patientEntity);
        verify(patientJpaRepository, times(1)).existsByEmail(patientEntity.getEmail());
        verify(patientJpaRepository, times(1)).existsByPesel(patientEntity.getPesel());
        verifyNoMoreInteractions(patientJpaRepository);
    }

    @Test
    public void testSave() {
        // given
        PatientEntity patientEntity = EntityFixtures.somePatient1();

        // when
        patientRepository.save(patientEntity);

        // then
        verify(patientJpaRepository, times(1)).save(patientEntity);
    }

    @Test
    public void testSaveAndReturn_UniquePatient() {
        // given
        PatientEntity patientEntity = EntityFixtures.somePatient1();

        BDDMockito.given(patientJpaRepository.existsByPesel(patientEntity.getPesel())).willReturn(false);
        BDDMockito.given(patientJpaRepository.existsByEmail(patientEntity.getEmail())).willReturn(false);
        BDDMockito.given(patientJpaRepository.saveAndFlush(patientEntity)).willReturn(patientEntity);

        // when
        PatientEntity savedPatient = patientRepository.saveAndReturn(patientEntity);

        // then
        assertEquals(patientEntity, savedPatient);

        BDDMockito.then(patientJpaRepository).should().saveAndFlush(patientEntity);
    }

    @Test
    public void testSaveAndReturn_DuplicatePesel() {
        // given
        PatientEntity patientEntity = EntityFixtures.somePatient4();

        BDDMockito.given(patientJpaRepository.existsByPesel(patientEntity.getPesel())).willReturn(true);

        // when, then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> patientRepository.saveAndReturn(patientEntity));
        assertEquals("W systemie znajduje się już ktoś z takim peselem", exception.getMessage());

        BDDMockito.then(patientJpaRepository).should(never()).save(patientEntity);
    }

    @Test
    public void testSaveAndReturn_DuplicateEmail() {
        // given
        PatientEntity patientEntity = EntityFixtures.somePatient3();

        BDDMockito.given(patientJpaRepository.existsByPesel(patientEntity.getPesel())).willReturn(false);
        BDDMockito.given(patientJpaRepository.existsByEmail(patientEntity.getEmail())).willReturn(true);

        // when, then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> patientRepository.saveAndReturn(patientEntity));
        assertEquals("W systemie znajduje się już ktoś z takim emailem", exception.getMessage());

        BDDMockito.then(patientJpaRepository).should(never()).save(patientEntity);
    }


    @Test
    public void testDelete() {
        // given
        PatientEntity patientEntity = new PatientEntity();

        // when
        patientRepository.delete(patientEntity);

        // then
        verify(patientJpaRepository, times(1)).delete(patientEntity);
    }


}