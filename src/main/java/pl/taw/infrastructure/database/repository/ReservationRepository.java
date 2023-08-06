package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.business.dao.ReservationDAO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.ReservationEntity;
import pl.taw.infrastructure.database.repository.jpa.ReservationJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.ReservationMapper;

import java.time.LocalDate;
import java.util.List;

@Repository
@AllArgsConstructor
public class ReservationRepository implements ReservationDAO {

    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public List<ReservationDTO> findAll() {
        return reservationJpaRepository.findAll().stream()
                .map(reservationMapper::mapFromEntity)
                .toList();
    }

    @Override
    public ReservationDTO findById(Integer reservationId) {
        return reservationJpaRepository.findById(reservationId)
                .map(reservationMapper::mapFromEntity)
                .orElseThrow(() -> new NotFoundException(
                        "Could not found reservation with id: [%s]".formatted(reservationId)));
    }

    @Override
    public ReservationEntity findEntityById(Integer reservationId) {
        return reservationJpaRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        "Could not found reservation with id: [%s]".formatted(reservationId)));
    }

    @Override
    public ReservationEntity saveAndReturn(ReservationEntity reservationEntity) {
        return reservationJpaRepository.saveAndFlush(reservationEntity);
    }

    @Override
    public void save(ReservationEntity reservationEntity) {
        reservationJpaRepository.save(reservationEntity);
    }

    @Override
    public void delete(ReservationEntity reservationEntity) {
        reservationJpaRepository.delete(reservationEntity);
    }

    @Override
    public List<ReservationDTO> findAllByDoctor(Integer doctorId) {
        return reservationJpaRepository.findAll().stream()
                .filter(reservation -> doctorId.equals(reservation.getDoctorId()))
                .map(reservationMapper::mapFromEntity)
                .toList();
    }

    @Override
    public List<ReservationDTO> findAllByPatient(Integer patientId) {
        return reservationJpaRepository.findAll().stream()
                .filter(reservation -> patientId.equals(reservation.getPatientId()))
                .map(reservationMapper::mapFromEntity)
                .toList();
    }

    @Override
    public List<ReservationDTO> findAllByDay(LocalDate day) {
        return reservationJpaRepository.findAll().stream()
                .filter(reservation -> day.equals(reservation.getDay()))
                .map(reservationMapper::mapFromEntity)
                .toList();
    }

    @Override
    public List<ReservationDTO> findAllByBoth(Integer doctorId, Integer patientId) {
        return reservationJpaRepository.findAll().stream()
                .filter(reservation -> doctorId.equals(reservation.getDoctorId()))
                .filter(reservation -> patientId.equals(reservation.getPatientId()))
                .map(reservationMapper::mapFromEntity)
                .toList();
    }
}
