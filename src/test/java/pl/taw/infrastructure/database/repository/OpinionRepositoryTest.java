package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.OpinionEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.infrastructure.database.repository.jpa.OpinionJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.OpinionMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.hamcrest.Matchers.contains;
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
        OpinionEntity opinionEntity1 = OpinionEntity.builder()
                .opinionId(1)
                .doctorId(1)
                .patientId(1)
                .comment("Jakaś opinia")
                .createdAt(LocalDateTime.of(2023,6,1,12,0,0))
                .visitId(1)
                .build();
        OpinionEntity opinionEntity2 = OpinionEntity.builder()
                .opinionId(2)
                .doctorId(10)
                .patientId(5)
                .comment("Jakaś opinia, cokolwiek")
                .createdAt(LocalDateTime.of(2023,6,1,14,0,0))
                .visitId(7)
                .build();
        OpinionEntity opinionEntity3 = OpinionEntity.builder()
                .opinionId(3)
                .doctorId(8)
                .patientId(3)
                .comment("Jakaś opinia, nie ważne")
                .createdAt(LocalDateTime.of(2023,6,1,12,0,0))
                .visitId(10)
                .build();
        List<OpinionEntity> opinions = Arrays.asList(opinionEntity1, opinionEntity2, opinionEntity3);
        List<OpinionDTO> opinionDTOS = opinions.stream().map(opinionMapper::mapFromEntity).toList();
        Mockito.doReturn(opinionDTOS).when(opinionJpaRepository).findAll();

        // when
        List<OpinionDTO> result = opinionRepository.findAll();

        // then
        verify(opinionJpaRepository, times(1)).findAll();
        assertEquals(3, result.size());
        assertTrue(result.contains(opinionDTOS.get(0)));
    }

    @Test
    void findAll_ReturnsAllOpinions() {
        // given
        OpinionEntity opinionEntity1 = OpinionEntity.builder()
                .opinionId(1)
                .doctorId(1)
                .patientId(1)
                .comment("Jakaś opinia")
                .createdAt(LocalDateTime.of(2023, 6, 1, 12, 0, 0))
                .visitId(1)
                .build();
        OpinionEntity opinionEntity2 = OpinionEntity.builder()
                .opinionId(2)
                .doctorId(10)
                .patientId(5)
                .comment("Jakaś opinia, cokolwiek")
                .createdAt(LocalDateTime.of(2023, 6, 1, 14, 0, 0))
                .visitId(7)
                .build();
        OpinionEntity opinionEntity3 = OpinionEntity.builder()
                .opinionId(3)
                .doctorId(8)
                .patientId(3)
                .comment("Jakaś opinia, nie ważne")
                .createdAt(LocalDateTime.of(2023, 6, 1, 12, 0, 0))
                .visitId(10)
                .build();
        List<OpinionEntity> opinions = Arrays.asList(opinionEntity1, opinionEntity2, opinionEntity3);
        List<OpinionDTO> opinionDTOS = opinions.stream().map(opinionMapper::mapFromEntity).toList();
        Mockito.doReturn(opinionDTOS).when(opinionJpaRepository).findAll();

        // when
        List<OpinionDTO> result = opinionRepository.findAll();

        // then
        verify(opinionJpaRepository, times(1)).findAll();
        assertEquals(opinionDTOS, result);
        assertFalse(result.isEmpty());
    }


    @Test
    void findById() {
        // given
        Integer opinionId = 3;
        OpinionEntity opinionEntity = OpinionEntity.builder()
                .opinionId(opinionId)
                .doctorId(1)
                .patientId(5)
                .visitId(7)
                .comment("Zdrowe podejście")
                .build();
        OpinionDTO opinionDTO = OpinionDTO.builder()
                .opinionId(opinionId)
                .doctorId(1)
                .patientId(5)
                .visitId(7)
                .comment("Zdrowe podejście")
                .build();
        Mockito.when(opinionJpaRepository.findById(opinionId)).thenReturn(Optional.of(opinionEntity));
        Mockito.when(opinionMapper.mapFromEntity(opinionEntity)).thenReturn(opinionDTO);

        // when
        OpinionDTO expectedOpinionDTO = opinionRepository.findById(opinionId);

        // then
        verify(opinionJpaRepository, times(1)).findById(opinionId);
        assertNotNull(expectedOpinionDTO);
        assertEquals(opinionEntity.getOpinionId(), expectedOpinionDTO.getOpinionId());
        assertEquals(opinionEntity.getDoctorId(), expectedOpinionDTO.getDoctorId());
        assertEquals(opinionEntity.getPatientId(), expectedOpinionDTO.getPatientId());
        assertEquals(opinionEntity.getVisitId(), expectedOpinionDTO.getVisitId());
        assertEquals(opinionEntity.getComment(), expectedOpinionDTO.getComment());
    }

    @Test
    void findEntityById() {
        // given
        OpinionEntity opinion = OpinionEntity.builder()
                .opinionId(5)
                .doctorId(2)
                .patientId(3)
                .visitId(8)
                .comment("Polecam")
                .build();
        Mockito.when(opinionJpaRepository.findById(opinion.getOpinionId())).thenReturn(Optional.of(opinion));

        // when
        OpinionEntity expectedOpinion = opinionRepository.findEntityById(opinion.getOpinionId());

        // then
        verify(opinionJpaRepository, times(1)).findById(opinion.getOpinionId());
        assertEquals(opinion, expectedOpinion);
        assertNotNull(expectedOpinion);
        assertEquals(opinion.getOpinionId(), expectedOpinion.getOpinionId());
        assertEquals(opinion.getDoctorId(), expectedOpinion.getDoctorId());
        assertEquals(opinion.getPatientId(), expectedOpinion.getPatientId());
        assertEquals(opinion.getVisitId(), expectedOpinion.getVisitId());
        assertEquals(opinion.getComment(), expectedOpinion.getComment());
    }

    @Test
    void saveAndReturn() {
        // given
        OpinionEntity opinion = OpinionEntity.builder()
                .opinionId(100)
                .doctorId(15)
                .patientId(30)
                .visitId(120)
                .comment("Polecam")
                .build();
        Mockito.when(opinionJpaRepository.save(opinion)).thenReturn(opinion);

        // when
        OpinionEntity savedOpinion = opinionRepository.saveAndReturn(opinion);

        // then
        verify(opinionJpaRepository, times(1)).save(opinion);
        assertNotNull(savedOpinion);
        assertEquals(opinion, savedOpinion);
    }

    @Test
    void save() {
        // given
        OpinionEntity opinion = OpinionEntity.builder()
                .opinionId(100)
                .doctorId(15)
                .patientId(30)
                .visitId(120)
                .comment("Polecam")
                .build();
        Mockito.when(opinionJpaRepository.findById(opinion.getOpinionId())).thenReturn(Optional.of(opinion));

        // when
        opinionRepository.save(opinion);

        OpinionEntity savedOpinion = opinionRepository.findEntityById(opinion.getOpinionId());


        // then
        verify(opinionJpaRepository, times(1)).save(opinion);
        assertNotNull(savedOpinion);
        assertEquals(opinion, savedOpinion);
    }

    @Test
    void delete() {
        // given
        OpinionEntity opinion = OpinionEntity.builder()
                .opinionId(10)
                .doctorId(5)
                .patientId(3)
                .visitId(15)
                .createdAt(LocalDateTime.now())
                .comment("Wszystko OK")
                .build();

        // when
        opinionRepository.delete(opinion);
        List<OpinionDTO> result = opinionRepository.findAll();

        // then
        verify(opinionJpaRepository, times(1)).delete(opinion);
        assertDoesNotThrow(() -> opinionRepository.delete(opinion));
        assertTrue(result.isEmpty());
        Mockito.verifyNoInteractions(opinionMapper);
    }

    @Test
    void findAllByDoctor() {
        // given
        Integer doctorId = 5;
        DoctorEntity doctorEntity = DoctorEntity.builder()
                .doctorId(doctorId)
                .build();

        OpinionEntity opinionEntity1 = OpinionEntity.builder()
                .opinionId(5)
                .doctorId(doctorId)
                .build();
        OpinionEntity opinionEntity2 = OpinionEntity.builder()
                .opinionId(8)
                .doctorId(doctorId)
                .build();

        List<OpinionEntity> opinionEntities = List.of(opinionEntity1, opinionEntity2);

        Mockito.when(opinionJpaRepository.findAll()).thenReturn(opinionEntities);

        OpinionDTO opinionDTO1 = OpinionDTO.builder()
                .opinionId(5)
                .doctorId(doctorId)
                .build();
        OpinionDTO opinionDTO2 = OpinionDTO.builder()
                .opinionId(8)
                .doctorId(doctorId)
                .build();

        Mockito.when(opinionMapper.mapFromEntity(opinionEntity1)).thenReturn(opinionDTO1);
        Mockito.when(opinionMapper.mapFromEntity(opinionEntity2)).thenReturn(opinionDTO2);

        List<OpinionDTO> expectedOpinions = Arrays.asList(opinionDTO1, opinionDTO2);

        // when
        List<OpinionDTO> result = opinionRepository.findAllByDoctor(doctorId);

        // then
        verify(opinionJpaRepository, times(1)).findAll();
        assertEquals(2, result.size());
        assertEquals(opinionDTO1, result.get(0));
        assertEquals(opinionDTO2, result.get(1));
        assertEquals(expectedOpinions, result);
    }

    @Test
    void findAllByPatient() {
        // given
        Integer patientId = 3;
        PatientEntity patientEntity = PatientEntity.builder()
                .patientId(patientId)
                .build();

        OpinionEntity opinionEntity1 = OpinionEntity.builder()
                .opinionId(2)
                .patientId(patientId)
                .build();
        OpinionEntity opinionEntity2 = OpinionEntity.builder()
                .opinionId(7)
                .patientId(patientId)
                .build();

        List<OpinionEntity> opinionEntities = List.of(opinionEntity1, opinionEntity2);

        Mockito.when(opinionJpaRepository.findAll()).thenReturn(opinionEntities);

        OpinionDTO opinionDTO1 = OpinionDTO.builder()
                .opinionId(2)
                .patientId(patientId)
                .build();
        OpinionDTO opinionDTO2 = OpinionDTO.builder()
                .opinionId(7)
                .patientId(patientId)
                .build();

        Mockito.when(opinionMapper.mapFromEntity(opinionEntity1)).thenReturn(opinionDTO1);
        Mockito.when(opinionMapper.mapFromEntity(opinionEntity2)).thenReturn(opinionDTO2);

        List<OpinionDTO> expectedOpinions = Arrays.asList(opinionDTO1, opinionDTO2);

        // when
        List<OpinionDTO> result = opinionRepository.findAllByPatient(patientId);

        // then
        verify(opinionJpaRepository, times(1)).findAll();
        assertEquals(2, result.size());
        assertEquals(opinionDTO1, result.get(0));
        assertEquals(opinionDTO2, result.get(1));
        assertEquals(expectedOpinions, result);
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

    private static Stream<Arguments> provideOpinionsByDoctorId() {
        List<OpinionEntity> opinions1 = new ArrayList<>();
        opinions1.add(new OpinionEntity().withDoctorId(1));
        opinions1.add(new OpinionEntity().withDoctorId(1));

        List<OpinionEntity> opinions2 = new ArrayList<>();
        opinions2.add(new OpinionEntity().withDoctorId(2));

        return Stream.of(
                Arguments.of(1, opinions1),
                Arguments.of(2, opinions2)
        );
    }

    private static Stream<Arguments> provideOpinionsByPatientId() {
        List<OpinionEntity> opinions1 = new ArrayList<>();
        opinions1.add(new OpinionEntity().withPatientId(1));
        opinions1.add(new OpinionEntity().withPatientId(1));

        List<OpinionEntity> opinions2 = new ArrayList<>();
        opinions2.add(new OpinionEntity().withPatientId(2));
        opinions2.add(new OpinionEntity().withPatientId(2));
        opinions2.add(new OpinionEntity().withPatientId(2));

        return Stream.of(
                Arguments.of(1, opinions1),
                Arguments.of(2, opinions2)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testNieWiemCo(String val1, String val2) {
        // given

        // when

        // then
    }

    private static Stream<Arguments> testNieWiemCo() {
        return Stream.of(
                Arguments.of("val1", "val2"),
                Arguments.of("val3", "val4"),
                Arguments.of("val5", "val6")
        );
    }
}