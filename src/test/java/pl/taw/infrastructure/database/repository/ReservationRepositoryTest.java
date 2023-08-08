package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.ReservationEntity;
import pl.taw.infrastructure.database.repository.jpa.ReservationJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.ReservationMapper;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationRepositoryTest {

    @InjectMocks
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationJpaRepository reservationJpaRepository;
    @Mock
    private ReservationMapper reservationMapper;


    @Test
    public void testFindAll() {
        // given
        ReservationEntity reservationEntity1 = EntityFixtures.someReservation1();
        ReservationEntity reservationEntity2 = EntityFixtures.someReservation2();
        List<ReservationEntity> reservationEntities = Stream.of(reservationEntity1, reservationEntity2).collect(Collectors.toList());

        ReservationDTO reservationDTO1 = DtoFixtures.someReservation1();
        ReservationDTO reservationDTO2 = DtoFixtures.someReservation2();
        List<ReservationDTO> expectedDTOs = Stream.of(reservationDTO1, reservationDTO2).collect(Collectors.toList());

        when(reservationJpaRepository.findAll()).thenReturn(reservationEntities);
        when(reservationMapper.mapFromEntity(reservationEntity1)).thenReturn(reservationDTO1);
        when(reservationMapper.mapFromEntity(reservationEntity2)).thenReturn(reservationDTO2);

        // when
        List<ReservationDTO> resultDTOs = reservationRepository.findAll();

        // then
        assertEquals(expectedDTOs, resultDTOs);

        verify(reservationJpaRepository, times(1)).findAll();
        verify(reservationMapper, times(1)).mapFromEntity(reservationEntity1);
        verify(reservationMapper, times(1)).mapFromEntity(reservationEntity2);
    }

    @Test
    public void testFindByIdExisting() {
        // given
        Integer reservationId = 3;
        ReservationEntity reservationEntity = EntityFixtures.someReservation3();
        ReservationDTO expectedDTO = DtoFixtures.someReservation3();

        when(reservationJpaRepository.findById(reservationId)).thenReturn(Optional.of(reservationEntity));
        when(reservationMapper.mapFromEntity(reservationEntity)).thenReturn(expectedDTO);

        // when
        ReservationDTO resultDTO = reservationRepository.findById(reservationId);

        // then
        assertEquals(expectedDTO, resultDTO);
        verify(reservationJpaRepository, times(1)).findById(reservationId);
        verify(reservationMapper, times(1)).mapFromEntity(reservationEntity);
    }

    @Test
    public void testFindByIdNonExisting() {
        // given
        Integer reservationId = 1;

        when(reservationJpaRepository.findById(reservationId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(NotFoundException.class, () -> reservationRepository.findById(reservationId));
        verify(reservationJpaRepository, times(1)).findById(reservationId);
        verify(reservationMapper, never()).mapFromEntity(any(ReservationEntity.class));
    }

    @Test
    public void testFindEntityById() {
        // given
        Integer reservationId = 1;
        ReservationEntity expectedEntity = EntityFixtures.someReservation1();
        when(reservationJpaRepository.findById(reservationId))
                .thenReturn(Optional.of(expectedEntity));

        // when
        ReservationEntity result = reservationRepository.findEntityById(reservationId);

        // then
        assertEquals(expectedEntity, result);
        verify(reservationJpaRepository, times(1)).findById(reservationId);
    }

    @Test
    public void testFindEntityByIdNonExisting() {
        // given
        Integer reservationId = 1;

        when(reservationJpaRepository.findById(reservationId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(NotFoundException.class, () -> reservationRepository.findEntityById(reservationId));
        verify(reservationJpaRepository, times(1)).findById(reservationId);
        verify(reservationMapper, never()).mapFromEntity(any(ReservationEntity.class));
    }


    @Test
    public void testSaveAndReturn() {
        // given
        ReservationEntity reservationEntity = EntityFixtures.someReservation2();

        when(reservationJpaRepository.saveAndFlush(reservationEntity))
                .thenReturn(reservationEntity);

        // when
        ReservationEntity result = reservationRepository.saveAndReturn(reservationEntity);

        // then
        assertEquals(reservationEntity, result);
        verify(reservationJpaRepository, times(1)).saveAndFlush(reservationEntity);
    }

    @Test
    void testSave() {
        // given
        ReservationEntity reservationEntity = EntityFixtures.someReservation3();

        // when
        reservationRepository.save(reservationEntity);

        // then
        verify(reservationJpaRepository, times(1)).save(reservationEntity);
    }

    @Test
    void testDelete() {
        // given
        ReservationEntity reservationEntity = EntityFixtures.someReservation1();

        // when
        reservationRepository.delete(reservationEntity);

        // then
        verify(reservationJpaRepository, times(1)).delete(reservationEntity);
    }

    @Test
    public void testFindAllByDoctor() {
        // given
        Integer doctorId = 1;
        ReservationEntity reservation1 = EntityFixtures.someReservation1().withDoctorId(doctorId);
        ReservationEntity reservation2 = EntityFixtures.someReservation2().withDoctorId(doctorId + 1);
        List<ReservationEntity> allReservations = new ArrayList<>();
        allReservations.add(reservation1);
        allReservations.add(reservation2);

        when(reservationJpaRepository.findAll()).thenReturn(allReservations);
        when(reservationMapper.mapFromEntity(reservation1)).thenReturn(new ReservationDTO());

        // when
        List<ReservationDTO> result = reservationRepository.findAllByDoctor(doctorId);

        // then
        assertEquals(1, result.size());
        verify(reservationJpaRepository, times(1)).findAll();
        verify(reservationMapper, times(1)).mapFromEntity(reservation1);
    }

    @Test
    void testFindAllByPatient() {
        // given
        Integer patientId = 5;
        List<ReservationEntity> reservations = EntityFixtures.someReservationList.stream().map(res -> res.withPatientId(patientId)).toList();
        List<ReservationDTO> reservationDTOS = DtoFixtures.reservations.stream().map(res -> res.withPatientId(patientId)).toList();

        when(reservationJpaRepository.findAll()).thenReturn(reservations);
        when(reservationMapper.mapFromEntity(reservations.get(0))).thenReturn(reservationDTOS.get(0));
        when(reservationMapper.mapFromEntity(reservations.get(1))).thenReturn(reservationDTOS.get(1));
        when(reservationMapper.mapFromEntity(reservations.get(2))).thenReturn(reservationDTOS.get(2));

        // when
        List<ReservationDTO> result = reservationRepository.findAllByPatient(patientId);

        // then
        assertEquals(reservationDTOS, result);
        verify(reservationJpaRepository, times(1)).findAll();
        verify(reservationMapper, times(3)).mapFromEntity(any(ReservationEntity.class));
    }

    @Test
    public void testFindAllByDay() {
        // given
        LocalDate targetDay = LocalDate.of(2023, 8, 8);
        ReservationEntity reservation1 = EntityFixtures.someReservation1().withDay(targetDay);
        ReservationEntity reservation2 = EntityFixtures.someReservation2().withDay(targetDay.plusDays(2));
        ReservationDTO reservationDTO = DtoFixtures.someReservation1().withDay(targetDay);
        List<ReservationEntity> allReservations = new ArrayList<>();
        allReservations.add(reservation1);
        allReservations.add(reservation2);

        when(reservationJpaRepository.findAll()).thenReturn(allReservations);
        when(reservationMapper.mapFromEntity(reservation1)).thenReturn(reservationDTO);

        // when
        List<ReservationDTO> result = reservationRepository.findAllByDay(targetDay);

        // then
        assertEquals(1, result.size());
        assertEquals(targetDay, result.get(0).getDay());
        verify(reservationJpaRepository, times(1)).findAll();
        verify(reservationMapper, times(1)).mapFromEntity(reservation1);
    }

    @Test
    public void testFindAllByBoth() {
        // given
        Integer targetDoctorId = 1;
        Integer targetPatientId = 2;
        ReservationEntity reservation1 = EntityFixtures.someReservation1().withDoctorId(targetDoctorId).withPatientId(targetPatientId);
        ReservationEntity reservation2 = EntityFixtures.someReservation2().withDoctorId(targetDoctorId + 1).withPatientId(targetPatientId);
        ReservationEntity reservation3 = EntityFixtures.someReservation3().withDoctorId(targetDoctorId).withPatientId(targetPatientId + 1);
        ReservationDTO expectedReservation = DtoFixtures.someReservation1().withDoctorId(targetDoctorId).withPatientId(targetPatientId);
        List<ReservationEntity> allReservations = new ArrayList<>();
        allReservations.add(reservation1);
        allReservations.add(reservation2);
        allReservations.add(reservation3);

        when(reservationJpaRepository.findAll()).thenReturn(allReservations);
        when(reservationMapper.mapFromEntity(reservation1)).thenReturn(expectedReservation);

        // when
        List<ReservationDTO> result = reservationRepository.findAllByBoth(targetDoctorId, targetPatientId);

        // then
        assertEquals(1, result.size());
        verify(reservationJpaRepository, times(1)).findAll();
        verify(reservationMapper, times(1)).mapFromEntity(reservation1);
    }
}