package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.repository.jpa.DoctorJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.DoctorMapper;
import pl.taw.util.EntityFixtures;

import java.util.List;
import java.util.Optional;

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
        Mockito.when(doctorJpaRepository.findAll()).thenReturn(doctors);

        List<DoctorDTO> expectedDoctorDTOs = doctors.stream().map(doctorMapper::mapFromEntity).toList();
        Mockito.when(doctorMapper.mapFromEntity(Mockito.any(DoctorEntity.class)))
                .thenReturn(new DoctorDTO());

        // when
        List<DoctorDTO> result = doctorRepository.findAll();

        // then
        Assertions.assertEquals(expectedDoctorDTOs.size(), result.size());
    }

    @Test
    public void testFindById() {
        // given
        Integer doctorId = 1;
        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setDoctorId(doctorId);

        Mockito.when(doctorJpaRepository.findById(doctorId)).thenReturn(Optional.of(doctorEntity));


        DoctorDTO ecpectedDoctorDTO = new DoctorDTO();
        Mockito.when(doctorMapper.mapFromEntity(doctorEntity)).thenReturn(ecpectedDoctorDTO);

        // when
        DoctorDTO result = doctorRepository.findById(doctorId);

        // then
        Assertions.assertEquals(ecpectedDoctorDTO.getDoctorId(), result.getDoctorId());
    }

    @Test
    public void testFindEntityById() {
        // given
        Integer doctorId = 1;
        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setDoctorId(doctorId);

        Mockito.when(doctorJpaRepository.findById(doctorId)).thenReturn(Optional.of(doctorEntity));

        // when
        DoctorEntity result = doctorRepository.findEntityById(doctorId);

        // then
        Assertions.assertEquals(doctorId, result.getDoctorId());
    }

    @Test
    public void testSaveAndReturn() {
        // given
        DoctorEntity doctorEntity = new DoctorEntity();
        DoctorEntity savedDoctorEntity = new DoctorEntity();
        savedDoctorEntity.setDoctorId(1);

        Mockito.when(doctorJpaRepository.saveAndFlush(doctorEntity)).thenReturn(savedDoctorEntity);

        // when
        DoctorEntity result = doctorRepository.saveAndReturn(doctorEntity);

        // then
        Assertions.assertEquals(savedDoctorEntity.getDoctorId(), result.getDoctorId());
    }

    @Test
    public void testSave() {
        // given
        DoctorEntity doctorEntity = new DoctorEntity();

        // when
        doctorRepository.save(doctorEntity);

        // then
        Mockito.verify(doctorJpaRepository, Mockito.times(1)).save(doctorEntity);
    }

    @Test
    public void testDelete() {
        // given
        DoctorEntity doctorEntity = new DoctorEntity();

        // when
        doctorRepository.delete(doctorEntity);

        // then
        Mockito.verify(doctorJpaRepository, Mockito.times(1)).delete(doctorEntity);
    }

    @Test
    void testFindByIdThrow() {
        // given
        int doctorId = 0;

        Mockito.when(doctorJpaRepository.findById(doctorId)).thenThrow(new RuntimeException("Id powinna być dodatnie"));

        // when, then
        Assertions.assertThrows(RuntimeException.class, () -> doctorRepository.findById(doctorId));
    }


}