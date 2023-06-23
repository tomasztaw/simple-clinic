package pl.taw.business.dao;

import pl.taw.api.dto.DoctorScheduleDTO;
import pl.taw.infrastructure.database.entity.DoctorScheduleEntity;

import java.util.List;

public interface DoctorScheduleDAO {

    DoctorScheduleEntity findById(Integer scheduleId);

    List<DoctorScheduleDTO> findScheduleByDoctorId(Integer doctorId);

}
