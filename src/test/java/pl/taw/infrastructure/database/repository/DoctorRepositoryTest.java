package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.repository.jpa.DoctorJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.DoctorMapper;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorRepositoryTest {

    @Mock
    private DoctorJpaRepository doctorJpaRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @InjectMocks
    private DoctorRepository doctorRepository;


    @Test
    public void testFindAll() {
        // given
        List<DoctorEntity> doctors = EntityFixtures.someDoctorList;
        List<DoctorDTO> expectedDoctorDTOs = doctors.stream().map(doctorMapper::mapFromEntity).toList();

        when(doctorJpaRepository.findAll()).thenReturn(doctors);
        when(doctorMapper.mapFromEntity(any(DoctorEntity.class)))
                .thenAnswer(invocation -> {
                    DoctorEntity entity = invocation.getArgument(0);
                    int index = doctors.indexOf(entity);
                    return expectedDoctorDTOs.get(index);
                });

        // when
        List<DoctorDTO> result = doctorRepository.findAll();

        // then
        Assertions.assertEquals(expectedDoctorDTOs.size(), result.size());

        verify(doctorJpaRepository, times(1)).findAll();
        verify(doctorMapper, times(doctors.size() * 2)).mapFromEntity(any(DoctorEntity.class));
        verifyNoMoreInteractions(doctorJpaRepository, doctorMapper);
    }

    @Test
    public void testFindById() {
        // given
        Integer doctorId = 1;
        DoctorEntity doctorEntity = EntityFixtures.someDoctor1();
        DoctorDTO ecpectedDoctorDTO = DtoFixtures.someDoctor1();

        when(doctorJpaRepository.findById(doctorId)).thenReturn(Optional.of(doctorEntity));
        when(doctorMapper.mapFromEntity(doctorEntity)).thenReturn(ecpectedDoctorDTO);

        // when
        DoctorDTO result = doctorRepository.findById(doctorId);

        // then
        Assertions.assertEquals(ecpectedDoctorDTO.getDoctorId(), result.getDoctorId());

        verify(doctorJpaRepository, times(1)).findById(doctorId);
        verify(doctorMapper, times(1)).mapFromEntity(doctorEntity);
        verifyNoMoreInteractions(doctorJpaRepository, doctorMapper);
    }

    @Test
    public void testFindEntityById() {
        // given
        Integer doctorId = 5;
        DoctorEntity doctorEntity = EntityFixtures.someDoctor5();

        when(doctorJpaRepository.findById(doctorId)).thenReturn(Optional.of(doctorEntity));

        // when
        DoctorEntity result = doctorRepository.findEntityById(doctorId);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(doctorId, result.getDoctorId());

        verify(doctorJpaRepository, times(1)).findById(doctorId);
        verifyNoMoreInteractions(doctorJpaRepository);
        verifyNoInteractions(doctorMapper);
    }

    @Test
    public void testSaveAndReturn() {
        // given
        DoctorEntity doctorEntity = EntityFixtures.someDoctor2();
        DoctorEntity savedDoctorEntity = doctorEntity.withName("Agnes");

        when(doctorJpaRepository.saveAndFlush(doctorEntity)).thenReturn(savedDoctorEntity);

        // when
        DoctorEntity result = doctorRepository.saveAndReturn(doctorEntity);

        // then
        Assertions.assertEquals(savedDoctorEntity.getDoctorId(), result.getDoctorId());

        verify(doctorJpaRepository, times(1)).saveAndFlush(doctorEntity);
        verifyNoMoreInteractions(doctorJpaRepository);
    }

    @Test
    public void testSave() {
        // given
        DoctorEntity doctorEntity = EntityFixtures.someDoctor6();

        // when
        doctorRepository.save(doctorEntity);

        // then
        verify(doctorJpaRepository, times(1)).save(doctorEntity);
        verifyNoMoreInteractions(doctorJpaRepository);
    }

    @Test
    public void testDelete() {
        // given
        DoctorEntity doctorEntity = EntityFixtures.someDoctor4();

        // when
        doctorRepository.delete(doctorEntity);

        // then
        verify(doctorJpaRepository, times(1)).delete(doctorEntity);
        verifyNoMoreInteractions(doctorJpaRepository);
    }

    @Test
    void testFindByIdThrow() {
        // given
        int doctorId = 0;

        when(doctorJpaRepository.findById(doctorId)).thenThrow(new RuntimeException("Id powinna być dodatnie"));

        // when, then
        Assertions.assertThrows(RuntimeException.class, () -> doctorRepository.findById(doctorId));
        verifyNoMoreInteractions(doctorJpaRepository);
    }


}