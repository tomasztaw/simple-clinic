package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.taw.api.dto.DoctorScheduleDTO;
import pl.taw.business.dao.DoctorScheduleDAO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.DoctorScheduleEntity;
import pl.taw.infrastructure.database.repository.jpa.DoctorScheduleJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.DoctorScheduleMapper;

import java.util.List;

@Repository
@AllArgsConstructor
public class DoctorScheduleRepository implements DoctorScheduleDAO {

    private final DoctorScheduleJpaRepository doctorScheduleJpaRepository;
    private final DoctorScheduleMapper doctorScheduleMapper;

    @Override
    public DoctorScheduleEntity findById(Integer scheduleId) {
        return doctorScheduleJpaRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException(
                        "Could not found DoctorScheduleEntity with id: [%s]".formatted(scheduleId)
                ));
    }

    @Override
    public List<DoctorScheduleDTO> findScheduleByDoctorId(Integer doctorId) {
        return doctorScheduleJpaRepository.findAll().stream()
                .filter(schedule -> doctorId.equals(schedule.getDoctorId()))
                .map(doctorScheduleMapper::mapFromEntity)
                .toList();
    }

}
