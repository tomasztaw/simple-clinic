package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.infrastructure.database.entity.OpinionEntity;
import pl.taw.infrastructure.database.repository.jpa.OpinionJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.OpinionMapper;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpinionRepositoryTest {

    @InjectMocks
    private OpinionRepository opinionRepository;

    @Mock
    private OpinionJpaRepository opinionJpaRepository;
    @Mock
    private OpinionMapper opinionMapper;


    @Test
    void findAll() {
        // given
        OpinionEntity opinionEntity1 = EntityFixtures.someOpinion1();
        OpinionEntity opinionEntity2 = EntityFixtures.someOpinion2();
        OpinionEntity opinionEntity3 = EntityFixtures.someOpinion2().withOpinionId(3).withVisitId(10);
        List<OpinionEntity> opinions = Arrays.asList(opinionEntity1, opinionEntity2, opinionEntity3);

        OpinionDTO opinionDTO1 = DtoFixtures.someOpinion1();
        OpinionDTO opinionDTO2 = DtoFixtures.someOpinion2();
        OpinionDTO opinionDTO3 = DtoFixtures.someOpinion2().withOpinionId(3).withVisitId(10);
        List<OpinionDTO> opinionDTOS = Arrays.asList(opinionDTO1, opinionDTO2, opinionDTO3);

        when(opinionMapper.mapFromEntity(ArgumentMatchers.any(OpinionEntity.class)))
                .thenAnswer(invocation -> {
                    OpinionEntity entity = invocation.getArgument(0);
                    int index = opinions.indexOf(entity);
                    return opinionDTOS.get(index);
                });
        when(opinionJpaRepository.findAll()).thenReturn(opinions);

        // when
        List<OpinionDTO> result = opinionRepository.findAll();

        // then
        assertEquals(3, result.size());
        assertTrue(result.contains(opinionDTOS.get(0)));

        verify(opinionJpaRepository, times(1)).findAll();
        verify(opinionMapper, times(opinions.size())).mapFromEntity(ArgumentMatchers.any(OpinionEntity.class));
        verifyNoMoreInteractions(opinionJpaRepository, opinionMapper);
    }

    @Test
    public void testFindAll_ThrowsException() {
        // Given
        when(opinionJpaRepository.findAll()).thenThrow(new RuntimeException("Database connection error"));

        // When, Then
        assertThrows(RuntimeException.class, () -> opinionRepository.findAll());
    }


    @Test
    void findById() {
        // given
        Integer opinionId = 2;
        OpinionEntity opinionEntity = EntityFixtures.someOpinion2();
        OpinionDTO opinionDTO = DtoFixtures.someOpinion2().withDoctorId(opinionEntity.getDoctorId())
                .withPatientId(opinionEntity.getPatientId()).withVisitId(opinionEntity.getVisitId())
                .withComment(opinionEntity.getComment());

        when(opinionJpaRepository.findById(opinionId)).thenReturn(Optional.of(opinionEntity));
        when(opinionMapper.mapFromEntity(opinionEntity)).thenReturn(opinionDTO);

        // when
        OpinionDTO expectedOpinionDTO = opinionRepository.findById(opinionId);

        // then
        assertNotNull(expectedOpinionDTO);
        assertEquals(opinionEntity.getOpinionId(), expectedOpinionDTO.getOpinionId());
        assertEquals(opinionEntity.getDoctorId(), expectedOpinionDTO.getDoctorId());
        assertEquals(opinionEntity.getPatientId(), expectedOpinionDTO.getPatientId());
        assertEquals(opinionEntity.getVisitId(), expectedOpinionDTO.getVisitId());
        assertEquals(opinionEntity.getComment(), expectedOpinionDTO.getComment());

        verify(opinionJpaRepository, times(1)).findById(opinionId);
        verify(opinionMapper, times(1)).mapFromEntity(opinionEntity);
        verifyNoMoreInteractions(opinionJpaRepository, opinionMapper);
    }

    @Test
    void findEntityById() {
        // given
        OpinionEntity opinion = EntityFixtures.someOpinion2();

        when(opinionJpaRepository.findById(opinion.getOpinionId())).thenReturn(Optional.of(opinion));

        // when
        OpinionEntity expectedOpinion = opinionRepository.findEntityById(opinion.getOpinionId());

        // then
        assertEquals(opinion, expectedOpinion);
        assertNotNull(expectedOpinion);
        assertEquals(opinion.getOpinionId(), expectedOpinion.getOpinionId());
        assertEquals(opinion.getDoctorId(), expectedOpinion.getDoctorId());
        assertEquals(opinion.getPatientId(), expectedOpinion.getPatientId());
        assertEquals(opinion.getVisitId(), expectedOpinion.getVisitId());
        assertEquals(opinion.getComment(), expectedOpinion.getComment());

        verify(opinionJpaRepository, times(1)).findById(opinion.getOpinionId());
        verifyNoMoreInteractions(opinionJpaRepository);
        verifyNoInteractions(opinionMapper);
    }

    @Test
    void saveAndReturn() {
        // given
        OpinionEntity opinion = EntityFixtures.someOpinion1();
        OpinionEntity savedOpinionEntity = opinion.withComment("Polecam");

        when(opinionJpaRepository.save(opinion)).thenReturn(savedOpinionEntity);

        // when
        OpinionEntity savedOpinion = opinionRepository.saveAndReturn(opinion);

        // then
        assertNotNull(savedOpinion);
        assertEquals(savedOpinionEntity, savedOpinion);

        verify(opinionJpaRepository, times(1)).save(opinion);
        verifyNoMoreInteractions(opinionJpaRepository);
    }

    @Test
    void save() {
        // given
        OpinionEntity opinion = EntityFixtures.someOpinion2();

        // when
        opinionRepository.save(opinion);

        // then
        verify(opinionJpaRepository, times(1)).save(opinion);
        verifyNoMoreInteractions(opinionJpaRepository);
    }

    @Test
    void delete() {
        // given
        OpinionEntity opinion = EntityFixtures.someOpinion1();

        // when
        opinionRepository.delete(opinion);

        // then
        verify(opinionJpaRepository, times(1)).delete(opinion);
        verify(opinionJpaRepository, only()).delete(opinion);
    }

    @Test
    void deleteWithoutThrow() {
        // given
        OpinionEntity opinion = EntityFixtures.someOpinion1();

        // when, then
        assertDoesNotThrow(() -> opinionRepository.delete(opinion));

        verify(opinionJpaRepository, times(1)).delete(opinion);
        verifyNoInteractions(opinionMapper);
    }

    @Test
    void findAllByDoctor() {
        // given
        Integer doctorId = 5;
        OpinionEntity opinionEntity1 = EntityFixtures.someOpinion1().withDoctorId(doctorId);
        OpinionEntity opinionEntity2 = EntityFixtures.someOpinion2().withDoctorId(doctorId);
        List<OpinionEntity> opinionEntities = List.of(opinionEntity1, opinionEntity2);

        OpinionDTO opinionDTO1 = DtoFixtures.someOpinion1();
        OpinionDTO opinionDTO2 = DtoFixtures.someOpinion2();
        List<OpinionDTO> expectedOpinions = Arrays.asList(opinionDTO1, opinionDTO2);

        when(opinionJpaRepository.findAll()).thenReturn(opinionEntities);
        when(opinionMapper.mapFromEntity(opinionEntity1)).thenReturn(opinionDTO1);
        when(opinionMapper.mapFromEntity(opinionEntity2)).thenReturn(opinionDTO2);

        // when
        List<OpinionDTO> result = opinionRepository.findAllByDoctor(doctorId);

        // then
        assertEquals(2, result.size());
        assertEquals(opinionDTO1, result.get(0));
        assertEquals(opinionDTO2, result.get(1));
        assertEquals(expectedOpinions, result);

        verify(opinionJpaRepository, times(1)).findAll();
        verify(opinionMapper, times(result.size())).mapFromEntity(ArgumentMatchers.any(OpinionEntity.class));
        verifyNoMoreInteractions(opinionJpaRepository, opinionMapper);
    }

    @Test
    void findAllByPatient() {
        // given
        Integer patientId = 3;
        OpinionEntity opinionEntity1 = EntityFixtures.someOpinion1().withPatientId(patientId);
        OpinionEntity opinionEntity2 = EntityFixtures.someOpinion2().withPatientId(patientId);
        List<OpinionEntity> opinionEntities = List.of(opinionEntity1, opinionEntity2);

        OpinionDTO opinionDTO1 = DtoFixtures.someOpinion1().withPatientId(patientId);
        OpinionDTO opinionDTO2 = DtoFixtures.someOpinion2().withPatientId(patientId);
        List<OpinionDTO> expectedOpinions = Arrays.asList(opinionDTO1, opinionDTO2);

        when(opinionJpaRepository.findAll()).thenReturn(opinionEntities);
        when(opinionMapper.mapFromEntity(opinionEntity1)).thenReturn(opinionDTO1);
        when(opinionMapper.mapFromEntity(opinionEntity2)).thenReturn(opinionDTO2);

        // when
        List<OpinionDTO> result = opinionRepository.findAllByPatient(patientId);

        // then
        assertEquals(2, result.size());
        assertEquals(opinionDTO1, result.get(0));
        assertEquals(opinionDTO2, result.get(1));
        assertEquals(expectedOpinions, result);

        verify(opinionJpaRepository, times(1)).findAll();
        verify(opinionMapper, times(result.size())).mapFromEntity(ArgumentMatchers.any(OpinionEntity.class));
        verifyNoMoreInteractions(opinionJpaRepository, opinionMapper);
    }

    @ParameterizedTest
    @MethodSource("provideOpinionsByDoctorId")
    void testFindAllByDoctor(Integer doctorId, List<OpinionEntity> expectedOpinions) {
        // given
        when(opinionJpaRepository.findAll().stream()
                .filter(opinion -> doctorId.equals(opinion.getDoctorId()))
                .toList())
                .thenReturn(expectedOpinions);
        List<OpinionDTO> expectedDTOs = expectedOpinions.stream()
                .map(opinionMapper::mapFromEntity)
                .toList();

        // when
        List<OpinionDTO> result = opinionRepository.findAllByDoctor(doctorId);

        // then
        assertEquals(expectedDTOs.size(), result.size());
        assertTrue(result.containsAll(expectedDTOs));
        verify(opinionJpaRepository, times(1)).findAll();
    }

    @ParameterizedTest
    @MethodSource("provideOpinionsByPatientId")
    void testFindAllByPatient(Integer patientId, List<OpinionEntity> expectedOpinions) {
        // given
        when(opinionJpaRepository.findAll().stream()
                .filter(opinion -> patientId.equals(opinion.getPatientId()))
                .toList()).thenReturn(expectedOpinions);
        List<OpinionDTO> expectedDTOs = expectedOpinions.stream()
                .map(opinionMapper::mapFromEntity)
                .toList();

        // when
        List<OpinionDTO> result = opinionRepository.findAllByPatient(patientId);

        // then
        assertEquals(expectedDTOs.size(), result.size());
        assertTrue(result.containsAll(expectedDTOs));
        verify(opinionJpaRepository, times(1)).findAll();
    }

    @ParameterizedTest
    @MethodSource("provideOpinionsByDoctorId")
    void testFindAllByDoctorPT(Integer doctorId, List<OpinionEntity> expectedOpinions) {
        // given
        when(opinionJpaRepository.findAll().stream()
                .filter(opinion -> doctorId.equals(opinion.getDoctorId()))
                .toList()).thenReturn(expectedOpinions);
        List<OpinionDTO> expectedDTOs = expectedOpinions.stream()
                .map(opinionMapper::mapFromEntity)
                .toList();

        // when
        List<OpinionDTO> result = opinionRepository.findAllByDoctor(doctorId);

        // then
        assertEquals(expectedDTOs.size(), result.size());
        assertTrue(result.containsAll(expectedDTOs));
        verify(opinionJpaRepository, times(1)).findAll();
    }

    private static Stream<Arguments> provideOpinionsByDoctorId() {
        List<OpinionEntity> opinions1 = new ArrayList<>();
        opinions1.add(EntityFixtures.someOpinion1().withDoctorId(1));
        opinions1.add(EntityFixtures.someOpinion2().withDoctorId(1));

        List<OpinionEntity> opinions2 = new ArrayList<>();
        opinions2.add(EntityFixtures.someOpinion1().withDoctorId(2));

        return Stream.of(
                Arguments.of(1, opinions1),
                Arguments.of(2, opinions2)
        );
    }

    private static Stream<Arguments> provideOpinionsByPatientId() {
        Integer firstPatientId = 1, secondPatientId = 2;
        OpinionEntity opinion1 = EntityFixtures.someOpinion1().withPatientId(firstPatientId);
        OpinionEntity opinion2 = EntityFixtures.someOpinion2().withPatientId(firstPatientId);
        List<OpinionEntity> opinions1 = List.of(opinion1, opinion2);

        List<OpinionEntity> opinions2 = new ArrayList<>();
        opinions2.add(EntityFixtures.someOpinion1().withPatientId(secondPatientId));
        opinions2.add(EntityFixtures.someOpinion2().withPatientId(secondPatientId));
        opinions2.add(EntityFixtures.someOpinion2().withPatientId(secondPatientId).withComment("Trzecia opinia"));

        return Stream.of(
                Arguments.of(firstPatientId, opinions1),
                Arguments.of(secondPatientId, opinions2)
        );
    }

}