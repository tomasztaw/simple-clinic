package pl.taw.business.dao;

import pl.taw.api.dto.ReservationDTO;
import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.entity.ReservationEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;

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
}