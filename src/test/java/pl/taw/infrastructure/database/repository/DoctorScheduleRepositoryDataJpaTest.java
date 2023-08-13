package pl.taw.infrastructure.database.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import pl.taw.api.dto.DoctorScheduleDTO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.DoctorScheduleEntity;
import pl.taw.infrastructure.database.repository.jpa.DoctorScheduleJpaRepository;

@DataJpaTest
@ActiveProfiles("test")
public class DoctorScheduleRepositoryDataJpaTest extends AbstractJpaIT {

    @Autowired
    private DoctorScheduleJpaRepository doctorScheduleJpaRepository;

    private DoctorScheduleRepository doctorScheduleRepository;

    @BeforeEach
    public void setUp() {
        doctorScheduleRepository = new DoctorScheduleRepository(doctorScheduleJpaRepository, null); // You need to provide the DoctorScheduleMapper mock
    }

    @Test
    public void findById_ExistingScheduleId_ShouldReturnCorrectSchedule() {
        // Given
        DoctorScheduleEntity scheduleEntity = new DoctorScheduleEntity();
        // Initialize scheduleEntity with necessary fields
        DoctorScheduleEntity savedSchedule = doctorScheduleJpaRepository.save(scheduleEntity);

        // When
        DoctorScheduleEntity foundSchedule = doctorScheduleRepository.findById(savedSchedule.getScheduleId());

        // Then
        assertThat(foundSchedule).isEqualTo(savedSchedule);
    }

    @Test
    public void findById_NonExistingScheduleId_ShouldThrowNotFoundException() {
        // Given
        Integer nonExistingId = -1;

        // When & Then
        assertThrows(NotFoundException.class, () -> doctorScheduleRepository.findById(nonExistingId));
    }

    @Test
    public void findScheduleByDoctorId_ExistingDoctorId_ShouldReturnCorrectSchedules() {
        // Given
        Integer doctorId = 1; // Provide an existing doctorId in your database
        // Create and save DoctorScheduleEntity instances with doctorId

        // When
        List<DoctorScheduleDTO> schedules = doctorScheduleRepository.findScheduleByDoctorId(doctorId);

        // Then
        assertThat(schedules).isNotEmpty();
        // You can further validate the content of the returned schedules
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
