package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.DoctorScheduleDTO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.DoctorScheduleEntity;
import pl.taw.infrastructure.database.repository.jpa.DoctorScheduleJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.DoctorScheduleMapper;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

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


    @Test
    void testFindById_existingScheduleId_shouldReturnDoctorScheduleEntity() {
        // given
        Integer scheduleId = 1;
        DoctorScheduleEntity expectedScheduleEntity = EntityFixtures.someSchedule1();

        when(doctorScheduleJpaRepository.findById(scheduleId)).thenReturn(Optional.of(expectedScheduleEntity));

        // when
        DoctorScheduleEntity result = doctorScheduleRepository.findById(scheduleId);

        // then
        assertNotNull(result);
        assertSame(expectedScheduleEntity, result);

        verify(doctorScheduleJpaRepository, times(1)).findById(scheduleId);
        verifyNoMoreInteractions(doctorScheduleJpaRepository);
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
        DoctorScheduleEntity schedule1 = EntityFixtures.someSchedule1();
        DoctorScheduleEntity schedule2 = EntityFixtures.someSchedule2();
        List<DoctorScheduleEntity> scheduleEntities = Arrays.asList(schedule1, schedule2);
        DoctorScheduleDTO scheduleDTO1 = DtoFixtures.someSchedule1();
        DoctorScheduleDTO scheduleDTO2 = DtoFixtures.someSchedule2();
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
        verifyNoMoreInteractions(doctorScheduleJpaRepository, doctorScheduleMapper);
    }

    @Test
    void testFindScheduleByDoctorId_DoctorWithoutScheduleId_shouldReturnEmptyList() {
        // given
        Integer DoctorWithoutScheduleId = 100;
        List<DoctorScheduleEntity> emptyScheduleEntities = List.of();

        when(doctorScheduleJpaRepository.findAll()).thenReturn(emptyScheduleEntities);

        // when
        List<DoctorScheduleDTO> result = doctorScheduleRepository.findScheduleByDoctorId(DoctorWithoutScheduleId);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(doctorScheduleJpaRepository, times(1)).findAll();
        verifyNoMoreInteractions(doctorScheduleJpaRepository);
    }
}