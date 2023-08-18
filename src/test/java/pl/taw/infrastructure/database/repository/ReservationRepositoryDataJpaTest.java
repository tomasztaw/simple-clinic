package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.infrastructure.database.entity.ReservationEntity;
import pl.taw.infrastructure.database.repository.config.AbstractJpaIT;
import pl.taw.infrastructure.database.repository.jpa.ReservationJpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ReservationRepositoryDataJpaTest extends AbstractJpaIT {

    private ReservationRepository reservationRepository;

    private ReservationJpaRepository reservationJpaRepository;
    private TestEntityManager entityManager;

    @Test
    void testFindAllReservationsReturnsCorrectSize() {
        // given
        long expectedSize = reservationJpaRepository.count();

        // when
        List<ReservationDTO> result = reservationRepository.findAll();

        // then
        assertThat(result.size()).isEqualTo(expectedSize);
        assertThat(result).hasSize((int) expectedSize);
    }

    @Test
    void testPatientRepositoryFindByIdWorksCorrectly() {
        // given
        ReservationEntity reservation = ReservationEntity.builder()
                .doctorId(1)
                .patientId(2)
                .day(LocalDate.of(2023, 8, 18))
                .startTimeR(LocalTime.of(10, 10))
                .occupied(true)
                .build();
        Integer reservationId = (Integer) entityManager.persistAndGetId(reservation);

        // when
        ReservationDTO foundReservation = reservationRepository.findById(reservationId);

        // then
        Assertions.assertThat(foundReservation.getReservationId()).isEqualTo(reservationId);
        Assertions.assertThat(foundReservation.getDay()).isEqualTo(reservation.getDay());
        Assertions.assertThat(foundReservation.getStartTimeR()).isEqualTo(reservation.getStartTimeR());
    }

    @Test
    void testPatientRepositoryFindEntityByIdWorksCorrectly() {
        // given
        ReservationEntity reservation = ReservationEntity.builder()
                .doctorId(1)
                .patientId(2)
                .day(LocalDate.of(2023, 8, 18))
                .startTimeR(LocalTime.of(10, 10))
                .occupied(true)
                .build();
        Integer reservationId = (Integer) entityManager.persistAndGetId(reservation);

        // when
        ReservationEntity foundReservation = reservationRepository.findEntityById(reservationId);

        // then
        Assertions.assertThat(foundReservation.getReservationId()).isEqualTo(reservationId);
        Assertions.assertThat(foundReservation.getDay()).isEqualTo(reservation.getDay());
        Assertions.assertThat(foundReservation.getStartTimeR()).isEqualTo(reservation.getStartTimeR());
    }

    @Test
    void thatSaveAndReturnWorksCorrectly() {
        // given
        ReservationEntity reservation = ReservationEntity.builder()
                .doctorId(3)
                .patientId(5)
                .day(LocalDate.of(2023, 8, 18))
                .startTimeR(LocalTime.of(10, 50))
                .occupied(true)
                .build();

        // when
        ReservationEntity result = reservationRepository.saveAndReturn(reservation);

        // then
        assertNotNull(result.getPatientId());
        assertEquals(reservation, result);
    }

    @Test
    void thatSaveWorksCorrectly() {
        // given
        ReservationEntity reservation = ReservationEntity.builder()
                .doctorId(3)
                .patientId(5)
                .day(LocalDate.of(2023, 8, 18))
                .startTimeR(LocalTime.of(10, 50))
                .occupied(true)
                .build();

        // when
        reservationRepository.save(reservation);
        entityManager.flush();
        ReservationEntity savedReservation = entityManager.find(ReservationEntity.class, reservation.getReservationId());

        // then
        assertNotNull(reservation.getPatientId());
        assertNotNull(savedReservation);
        assertEquals(reservation.getDoctorId(), savedReservation.getDoctorId());
        assertEquals(reservation.getPatientId(), savedReservation.getPatientId());
        assertEquals(reservation.getDay(), savedReservation.getDay());
        assertEquals(reservation.getStartTimeR(), savedReservation.getStartTimeR());
    }

    @Test
    void thatDeleteWorksCorrectly() {
        // given
        ReservationEntity reservation = ReservationEntity.builder()
                .doctorId(4)
                .patientId(4)
                .day(LocalDate.of(2023, 8, 17))
                .startTimeR(LocalTime.of(12, 30))
                .occupied(true)
                .build();
        entityManager.persistAndFlush(reservation);

        // when
        reservationRepository.delete(reservation);
        entityManager.flush();

        // then
        assertNull(entityManager.find(ReservationEntity.class, reservation.getReservationId()));
    }

    @Test
    void thatFindAllByDoctorWorksCorrectly() {
        // given
        Integer doctorId = 4;
        ReservationEntity reservation1 = ReservationEntity.builder()
                .doctorId(doctorId)
                .patientId(4)
                .day(LocalDate.of(2023, 8, 15))
                .startTimeR(LocalTime.of(12, 30))
                .occupied(true)
                .build();
        ReservationEntity reservation2 = ReservationEntity.builder()
                .doctorId(doctorId)
                .patientId(5)
                .day(LocalDate.of(2023, 8, 16))
                .startTimeR(LocalTime.of(12, 30))
                .occupied(true)
                .build();
        ReservationEntity reservation3 = ReservationEntity.builder()
                .doctorId(5)
                .patientId(5)
                .day(LocalDate.of(2023, 8, 17))
                .startTimeR(LocalTime.of(12, 30))
                .occupied(true)
                .build();
        entityManager.persistAndFlush(reservation1);
        entityManager.persistAndFlush(reservation2);
        entityManager.persistAndFlush(reservation3);

        // when
        List<ReservationDTO> result = reservationRepository.findAllByDoctor(doctorId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(res -> doctorId.equals(res.getDoctorId()));
    }

    @Test
    void thatFindAllByPatientWorksCorrectly() {
        // given
        Integer patientId = 3;
        ReservationEntity reservation1 = ReservationEntity.builder()
                .doctorId(1)
                .patientId(patientId)
                .day(LocalDate.of(2023, 8, 15))
                .startTimeR(LocalTime.of(12, 30))
                .occupied(true)
                .build();
        ReservationEntity reservation2 = ReservationEntity.builder()
                .doctorId(2)
                .patientId(5)
                .day(LocalDate.of(2023, 8, 16))
                .startTimeR(LocalTime.of(12, 30))
                .occupied(true)
                .build();
        ReservationEntity reservation3 = ReservationEntity.builder()
                .doctorId(3)
                .patientId(patientId)
                .day(LocalDate.of(2023, 8, 17))
                .startTimeR(LocalTime.of(12, 30))
                .occupied(true)
                .build();
        entityManager.persistAndFlush(reservation1);
        entityManager.persistAndFlush(reservation2);
        entityManager.persistAndFlush(reservation3);

        // when
        List<ReservationDTO> result = reservationRepository.findAllByPatient(patientId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(res -> patientId.equals(res.getPatientId()));
    }

    @Test
    void thatFindAllByDayWorksCorrectly() {
        // given
        LocalDate checkingDate = LocalDate.of(2023, 8, 18);
        ReservationEntity reservation1 = ReservationEntity.builder()
                .doctorId(1)
                .patientId(4)
                .day(LocalDate.of(2023, 8, 15))
                .startTimeR(LocalTime.of(12, 30))
                .occupied(true)
                .build();
        ReservationEntity reservation2 = ReservationEntity.builder()
                .doctorId(2)
                .patientId(5)
                .day(checkingDate)
                .startTimeR(LocalTime.of(12, 30))
                .occupied(true)
                .build();
        ReservationEntity reservation3 = ReservationEntity.builder()
                .doctorId(5)
                .patientId(5)
                .day(checkingDate)
                .startTimeR(LocalTime.of(12, 30))
                .occupied(true)
                .build();
        entityManager.persistAndFlush(reservation1);
        entityManager.persistAndFlush(reservation2);
        entityManager.persistAndFlush(reservation3);

        // when
        List<ReservationDTO> result = reservationRepository.findAllByDay(checkingDate);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(res -> checkingDate.equals(res.getDay()));
    }

    @Test
    void thatFindAllByBothWorksCorrectly() {
        // given
        Integer doctorId = 4;
        Integer patientId = 3;
        ReservationEntity reservation1 = ReservationEntity.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .day(LocalDate.of(2023, 8, 15))
                .startTimeR(LocalTime.of(12, 30))
                .occupied(true)
                .build();
        ReservationEntity reservation2 = ReservationEntity.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .day(LocalDate.of(2023, 8, 16))
                .startTimeR(LocalTime.of(12, 30))
                .occupied(true)
                .build();
        ReservationEntity reservation3 = ReservationEntity.builder()
                .doctorId(5)
                .patientId(5)
                .day(LocalDate.of(2023, 8, 17))
                .startTimeR(LocalTime.of(12, 30))
                .occupied(true)
                .build();
        entityManager.persistAndFlush(reservation1);
        entityManager.persistAndFlush(reservation2);
        entityManager.persistAndFlush(reservation3);

        // when
        List<ReservationDTO> result = reservationRepository.findAllByBoth(doctorId, patientId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(res -> doctorId.equals(res.getDoctorId())
                && patientId.equals(res.getPatientId()));
    }

}
