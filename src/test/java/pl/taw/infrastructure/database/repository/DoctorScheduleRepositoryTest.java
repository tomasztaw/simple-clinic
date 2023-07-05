package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.DoctorScheduleDTO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.DoctorScheduleEntity;
import pl.taw.infrastructure.database.repository.jpa.DoctorScheduleJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.DoctorScheduleMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleRepositoryTest {

    @InjectMocks
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private DoctorScheduleJpaRepository doctorScheduleJpaRepository;

    @Mock
    private DoctorScheduleMapper doctorScheduleMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorScheduleRepository = new DoctorScheduleRepository(doctorScheduleJpaRepository, doctorScheduleMapper);
    }

    @Test
    void testFindById_existingScheduleId_shouldReturnDoctorScheduleEntity() {
        // given
        Integer scheduleId = 1;
        DoctorScheduleEntity expectedScheduleEntity = new DoctorScheduleEntity();
        when(doctorScheduleJpaRepository.findById(scheduleId)).thenReturn(Optional.of(expectedScheduleEntity));

        // when
        DoctorScheduleEntity result = doctorScheduleRepository.findById(scheduleId);

        // then
        assertNotNull(result);
        assertSame(expectedScheduleEntity, result);
    }

    @Test
    void testFindById_nonExistingScheduleId_shouldThrowNotFoundException() {
        // given
        Integer nonExistingScheduleId = 100;
        when(doctorScheduleJpaRepository.findById(nonExistingScheduleId)).thenReturn(Optional.empty());

        // then
        assertThrows(NotFoundException.class, () -> {
            // when
            doctorScheduleRepository.findById(nonExistingScheduleId);
        });
    }

    @Test
    void testFindScheduleByDoctorId_existingDoctorId_shouldReturnListOfDoctorScheduleDTO() {
        // given
        Integer doctorId = 1;
        DoctorScheduleEntity schedule1 = new DoctorScheduleEntity().withDoctorId(doctorId);
        DoctorScheduleEntity schedule2 = new DoctorScheduleEntity().withDoctorId(doctorId);
        List<DoctorScheduleEntity> scheduleEntities = Arrays.asList(schedule1, schedule2);
        DoctorScheduleDTO scheduleDTO1 = new DoctorScheduleDTO();
        DoctorScheduleDTO scheduleDTO2 = new DoctorScheduleDTO();
        List<DoctorScheduleDTO> expectedScheduleDTOs = Arrays.asList(scheduleDTO1, scheduleDTO2);

        when(doctorScheduleJpaRepository.findAll()).thenReturn(scheduleEntities);

        when(doctorScheduleMapper.mapFromEntity(schedule1)).thenReturn(scheduleDTO1);
        when(doctorScheduleMapper.mapFromEntity(schedule2)).thenReturn(scheduleDTO2);

        // when
        List<DoctorScheduleDTO> result = doctorScheduleRepository.findScheduleByDoctorId(doctorId);

        // then
        assertNotNull(scheduleEntities);
        assertEquals(2, scheduleEntities.size());
        assertNotNull(result);
        assertEquals(expectedScheduleDTOs.size(), result.size());
        assertTrue(result.contains(scheduleDTO1));
        assertTrue(result.contains(scheduleDTO2));
        verify(doctorScheduleJpaRepository, times(1)).findAll();
        verify(doctorScheduleMapper, times(2)).mapFromEntity(any(DoctorScheduleEntity.class));
    }

    @Test
    void testFindScheduleByDoctorId_nonExistingDoctorId_shouldReturnEmptyList() {
        // given
        Integer nonExistingDoctorId = 100;
        List<DoctorScheduleEntity> emptyScheduleEntities = List.of();
        when(doctorScheduleJpaRepository.findAll()).thenReturn(emptyScheduleEntities);

        // when
        List<DoctorScheduleDTO> result = doctorScheduleRepository.findScheduleByDoctorId(nonExistingDoctorId);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}