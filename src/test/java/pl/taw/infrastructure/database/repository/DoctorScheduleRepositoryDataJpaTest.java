package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.taw.api.dto.DoctorScheduleDTO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.DoctorScheduleEntity;
import pl.taw.infrastructure.database.repository.config.AbstractJpaIT;
import pl.taw.infrastructure.database.repository.jpa.DoctorScheduleJpaRepository;
import pl.taw.util.EntityFixtures;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DoctorScheduleRepositoryDataJpaTest extends AbstractJpaIT {

    private DoctorScheduleRepository doctorScheduleRepository;
    private DoctorScheduleJpaRepository doctorScheduleJpaRepository;


    @Test
    public void findById_ExistingScheduleId_ShouldReturnCorrectSchedule() {
        // given
        DoctorScheduleEntity scheduleEntity = EntityFixtures.someSchedule1();
        DoctorScheduleEntity savedSchedule = doctorScheduleJpaRepository.save(scheduleEntity);

        // when
        DoctorScheduleEntity foundSchedule = doctorScheduleRepository.findById(savedSchedule.getScheduleId());

        // then
        assertThat(foundSchedule).isEqualTo(savedSchedule);
    }

    @Test
    public void findById_NonExistingScheduleId_ShouldThrowNotFoundException() {
        // given
        Integer nonExistingId = -1;

        // when, then
        assertThrows(NotFoundException.class, () -> doctorScheduleRepository.findById(nonExistingId));
    }

    @Test
    public void findScheduleByDoctorId_ExistingDoctorId_ShouldReturnCorrectSchedules() {
        // given
        Integer doctorId = 1;

        // when
        List<DoctorScheduleDTO> schedules = doctorScheduleRepository.findScheduleByDoctorId(doctorId);

        // then
        assertThat(schedules).isNotEmpty();
    }

    @Test
    void testFindScheduleByDoctorId_ExistingDoctorId_ShouldReturnCorrectSchedules() {
        // given
        DoctorEntity doctorEntity = EntityFixtures.someDoctor7().withEmail("nowy@eclinic.pl").withPhone("+48 120 222 228");
        DoctorScheduleEntity schedule1 = EntityFixtures.someSchedule1().withDoctorId(doctorEntity.getDoctorId()).withDoctor(doctorEntity);
        doctorScheduleJpaRepository.saveAndFlush(schedule1);
        DoctorScheduleEntity schedule2 = EntityFixtures.someSchedule2().withDoctorId(doctorEntity.getDoctorId()).withDoctor(doctorEntity);
        doctorScheduleJpaRepository.saveAndFlush(schedule2);

        // when
        List<DoctorScheduleDTO> schedules = doctorScheduleRepository.findScheduleByDoctorId(doctorEntity.getDoctorId());

        // then
        assertThat(schedules).isNotEmpty();
        assertThat(schedules).hasSize(5);
        assertThat(schedules).allMatch(schedule -> schedule.getDoctorId().equals(doctorEntity.getDoctorId()));
    }

    @Test
    public void findScheduleByDoctorId_NonExistingDoctorId_ShouldReturnEmptyList() {
        // Given
        Integer nonExistingDoctorId = -1;

        // When
        List<DoctorScheduleDTO> schedules = doctorScheduleRepository.findScheduleByDoctorId(nonExistingDoctorId);

        // Then
        assertThat(schedules).isEmpty();
    }
}
