package pl.taw.business.dao;

import pl.taw.api.dto.ReservationDTO;
import pl.taw.infrastructure.database.entity.ReservationEntity;

import java.time.LocalDate;
import java.util.List;

public interface ReservationDAO {

    List<ReservationDTO> findAll();

    ReservationDTO findById(Integer reservationId);

    ReservationEntity findEntityById(Integer reservationId);

    ReservationEntity saveAndReturn(ReservationEntity reservationEntity);

    void save(ReservationEntity reservationEntity);

    void delete(ReservationEntity reservationEntity);

    List<ReservationDTO> findAllByDoctor(Integer doctorId);

    List<ReservationDTO> findAllByPatient(Integer patientId);

    List<ReservationDTO> findAllByDay(LocalDate day);

    List<ReservationDTO> findAllByBoth(Integer doctorId, Integer patientId);
}
