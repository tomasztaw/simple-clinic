package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.PatientDTO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.jpa.PatientJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.PatientMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
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
        assertEquals(expectedPatientDTOs.size(), result.size());
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
        assertEquals(patientId, result.getPatientId());
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
        assertEquals(patientId, result.getPatientId());
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
        assertEquals(expectedPatientDTO, result);
        assertEquals(pesel.length(), result.getPesel().length());
    }


    @Test
    public void testSaveAndReturn() {
        // when
        PatientEntity patientEntity = new PatientEntity();
        PatientEntity savedPatientEntity = new PatientEntity();
        savedPatientEntity.setPatientId(1);

        Mockito.when(patientJpaRepository.saveAndFlush(patientEntity)).thenReturn(savedPatientEntity);

        // then
        PatientEntity result = patientRepository.saveAndReturn(patientEntity);

        // given
        assertEquals(savedPatientEntity.getPatientId(), result.getPatientId());
    }

    @Test
    public void testSave() {
        // given
        PatientEntity patientEntity = new PatientEntity();

        // when
        patientRepository.save(patientEntity);

        // then
        verify(patientJpaRepository, Mockito.times(1)).save(patientEntity);
    }

    @Test
    @Disabled("Muszę do tego wrócić")
    public void testSaveAndReturn_UniquePatient() {
        // given
        PatientEntity patientEntity = PatientEntity.builder()
                .name("Stefan")
                .surname("Nowak")
                .pesel("80101010266")
                .email("stefannowak@jakis.pl")
                .build();

        BDDMockito.given(patientJpaRepository.existsByPesel(patientEntity.getPesel())).willReturn(false);
        BDDMockito.given(patientJpaRepository.existsByEmail(patientEntity.getEmail())).willReturn(false);
        BDDMockito.given(patientJpaRepository.saveAndFlush(patientEntity)).willReturn(patientEntity);

        // when
        PatientEntity savedPatient = patientRepository.saveAndReturn(patientEntity);

        // then
        assertEquals(patientEntity, savedPatient);
        BDDMockito.then(patientJpaRepository).should().save(patientEntity);
    }

    @Test
    public void testSaveAndReturn_DuplicatePesel() {
        // given
        PatientEntity patientEntity = PatientEntity.builder()
                .name("Stefan")
                .surname("Nowak")
                .pesel("80101010266")
                .email("stefannowak@jakis.pl")
                .build();

        BDDMockito.given(patientJpaRepository.existsByPesel(patientEntity.getPesel())).willReturn(true);

        // when, then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> patientRepository.saveAndReturn(patientEntity));
        assertEquals("W systemie znajduje się już ktoś z takim peselem", exception.getMessage());
        BDDMockito.then(patientJpaRepository).should(Mockito.never()).save(patientEntity);
    }

    @Test
    public void testSaveAndReturn_DuplicateEmail() {
        // given
        PatientEntity patientEntity = PatientEntity.builder()
                .name("Stefan")
                .surname("Nowak")
                .pesel("80101010266")
                .email("stefannowak@jakis.pl")
                .build();

        BDDMockito.given(patientJpaRepository.existsByPesel(patientEntity.getPesel())).willReturn(false);
        BDDMockito.given(patientJpaRepository.existsByEmail(patientEntity.getEmail())).willReturn(true);

        // when, then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> patientRepository.saveAndReturn(patientEntity));
        assertEquals("W systemie znajduje się już ktoś z takim emailem", exception.getMessage());
        BDDMockito.then(patientJpaRepository).should(Mockito.never()).save(patientEntity);
    }


    @Test
    public void testDelete() {
        // given
        PatientEntity patientEntity = new PatientEntity();

        // when
        patientRepository.delete(patientEntity);

        // then
        verify(patientJpaRepository, Mockito.times(1)).delete(patientEntity);
    }


}