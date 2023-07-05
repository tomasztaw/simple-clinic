package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.repository.jpa.DoctorJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.DoctorMapper;
import pl.taw.util.EntityFixtures;

import java.util.List;
import java.util.Optional;

class DoctorRepositoryTest {

    @Mock
    private DoctorJpaRepository doctorJpaRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @InjectMocks
    private DoctorRepository doctorRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        // when
        List<DoctorEntity> doctors = EntityFixtures.someDoctorList;
        Mockito.when(doctorJpaRepository.findAll()).thenReturn(doctors);

        List<DoctorDTO> expectedDoctorDTOs = doctors.stream().map(doctorMapper::mapFromEntity).toList();
        Mockito.when(doctorMapper.mapFromEntity(Mockito.any(DoctorEntity.class)))
                .thenReturn(new DoctorDTO());

        // then
        List<DoctorDTO> result = doctorRepository.findAll();

        // given
        Assertions.assertEquals(expectedDoctorDTOs.size(), result.size());
    }

    @Test
    public void testFindById() {
        // when
        Integer doctorId = 1;
        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setDoctorId(doctorId);

        Mockito.when(doctorJpaRepository.findById(doctorId)).thenReturn(Optional.of(doctorEntity));


        DoctorDTO ecpectedDoctorDTO = new DoctorDTO();
        Mockito.when(doctorMapper.mapFromEntity(doctorEntity)).thenReturn(ecpectedDoctorDTO);

        // then
        DoctorDTO result = doctorRepository.findById(doctorId);

        // given
        Assertions.assertEquals(ecpectedDoctorDTO.getDoctorId(), result.getDoctorId());
    }

    @Test
    public void testFindEntityById() {
        // when
        Integer doctorId = 1;
        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setDoctorId(doctorId);

        Mockito.when(doctorJpaRepository.findById(doctorId)).thenReturn(Optional.of(doctorEntity));

        // then
        DoctorEntity result = doctorRepository.findEntityById(doctorId);

        // given
        Assertions.assertEquals(doctorId, result.getDoctorId());
    }

    @Test
    public void testSaveAndReturn() {
        // when
        DoctorEntity doctorEntity = new DoctorEntity();
        DoctorEntity savedDoctorEntity = new DoctorEntity();
        savedDoctorEntity.setDoctorId(1);

        Mockito.when(doctorJpaRepository.save(doctorEntity)).thenReturn(savedDoctorEntity);

        // then
        DoctorEntity result = doctorRepository.saveAndReturn(doctorEntity);

        // given
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