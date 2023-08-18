package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.infrastructure.database.repository.config.AbstractJpaIT;
import pl.taw.infrastructure.database.repository.jpa.VisitJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@AllArgsConstructor(onConstructor = @__(@Autowired))
class VisitRepositoryDataJpaTest extends AbstractJpaIT {

    private VisitRepository visitRepository;

    private VisitJpaRepository visitJpaRepository;
    private TestEntityManager entityManager;

    @Test
    void findAllWorksCorrectly() {
        // given
        long expectedSize = visitJpaRepository.count();

        // when
        List<VisitDTO> result = visitRepository.findAll();

        // then
        assertThat(result).hasSize((int) expectedSize);
    }

    @Test
    void testFindAllWithPaginationWorksCorrectly() {
        // given
        long expectedSize = visitJpaRepository.count();
        Pageable pageable = Pageable.ofSize(5);

        // when
        Page<VisitDTO> visitPage = visitRepository.findAll(pageable);

        // then
        assertThat(visitPage).isNotEmpty();
        assertThat(visitPage.getContent()).hasSize(pageable.getPageSize());
        assertThat(visitPage.getTotalElements()).isEqualTo(expectedSize);
    }

    @Test
    void findByIdWorksCorrectly() {
        // given
        VisitEntity visit = VisitEntity.builder()
                .doctorId(1)
                .patientId(2)
                .note("Notatka do wizyty")
                .dateTime(LocalDateTime.of(2023, 8, 18, 9, 10))
                .status("odbyta")
                .build();
        Integer visitId = (Integer) entityManager.persistAndGetId(visit);

        // when
        VisitDTO foundVisit = visitRepository.findById(visitId);

        // then
        Assertions.assertThat(foundVisit.getVisitId()).isEqualTo(visitId);
        Assertions.assertThat(foundVisit.getDoctorId()).isEqualTo(visit.getDoctorId());
        Assertions.assertThat(foundVisit.getPatientId()).isEqualTo(visit.getPatientId());
        Assertions.assertThat(foundVisit.getNote()).isEqualTo(visit.getNote());
        Assertions.assertThat(foundVisit.getDateTime()).isEqualTo(visit.getDateTime());
    }

    @Test
    void findEntityByIdWorksCorrectly() {
        // given
        VisitEntity visit = VisitEntity.builder()
                .doctorId(2)
                .patientId(3)
                .note("Pacjent ledwo żywy")
                .dateTime(LocalDateTime.of(2023, 8, 16, 9, 10))
                .status("odbyta")
                .build();
        Integer visitId = (Integer) entityManager.persistAndGetId(visit);

        // when
        VisitEntity foundVisit = visitRepository.findEntityById(visitId);

        // then
        Assertions.assertThat(foundVisit.getVisitId()).isEqualTo(visitId);
        Assertions.assertThat(foundVisit.getDoctorId()).isEqualTo(visit.getDoctorId());
        Assertions.assertThat(foundVisit.getPatientId()).isEqualTo(visit.getPatientId());
        Assertions.assertThat(foundVisit.getNote()).isEqualTo(visit.getNote());
        Assertions.assertThat(foundVisit.getDateTime()).isEqualTo(visit.getDateTime());
    }

    @Test
    void saveAndReturnWorksCorrectly() {
        // given
        VisitEntity visit = VisitEntity.builder()
                .doctorId(1)
                .patientId(2)
                .note("Notatka do wizyty, i tak dalej")
                .dateTime(LocalDateTime.of(2023, 8, 18, 9, 10))
                .status("odbyta")
                .build();

        // when
        VisitEntity result = visitRepository.saveAndReturn(visit);

        // then
        assertNotNull(result.getPatientId());
        assertEquals(visit, result);
    }

    @Test
    void saveWorksCorrectly() {
        // given
        VisitEntity visit = VisitEntity.builder()
                .doctorId(1)
                .patientId(2)
                .note("Notatka do wizyty, i tak dalej")
                .dateTime(LocalDateTime.of(2023, 8, 18, 9, 10))
                .status("odbyta")
                .build();

        // when
        visitRepository.save(visit);
        entityManager.flush();
        VisitEntity savedVisit = entityManager.find(VisitEntity.class, visit.getVisitId());

        // then
        assertNotNull(visit.getPatientId());
        assertNotNull(savedVisit);
        assertEquals(visit.getDoctorId(), savedVisit.getDoctorId());
        assertEquals(visit.getPatientId(), savedVisit.getPatientId());
        assertEquals(visit.getNote(), savedVisit.getNote());
        assertEquals(visit.getDateTime(), savedVisit.getDateTime());
    }

    @Test
    void deleteWorksCorrectly() {
        // given
        VisitEntity visit = VisitEntity.builder()
                .doctorId(1)
                .patientId(2)
                .note("Notatka do wizyty, i tak dalej. Nic wielkiego się nie stało")
                .dateTime(LocalDateTime.of(2023, 8, 15, 13, 20))
                .status("odbyta")
                .build();
        entityManager.persistAndFlush(visit);

        // when
        visitRepository.delete(visit);
        entityManager.flush();

        // then
        assertNull(entityManager.find(VisitEntity.class, visit.getVisitId()));
    }

    @Test
    void findAllByDoctorWorksCorrectly() {
        // given
        DoctorEntity doctor = DoctorEntity.builder()
                .name("Tadeusz")
                .surname("Ludwikowski")
                .title("Onkolog")
                .email("t.ludwikoski@eclinic.pl")
                .phone("+48 120 121 987")
                .build();
        PatientEntity patient = PatientEntity.builder()
                .name("Dominik")
                .surname("Bielski")
                .pesel("81101747896")
                .email("dominik.bielski@interia.pl")
                .phone("+48 123 444 777")
                .build();

        Integer doctorId = (Integer) entityManager.persistAndGetId(doctor);
        Integer patientId = (Integer) entityManager.persistAndGetId(patient);

        VisitEntity visit1 = VisitEntity.builder()
                .doctorId(doctorId)
                .doctor(doctor)
                .patientId(patientId)
                .patient(patient)
                .note("Notatka1")
                .dateTime(LocalDateTime.of(2023, 8, 15, 9, 10))
                .status("odbyta")
                .build();
        VisitEntity visit2 = VisitEntity.builder()
                .doctorId(doctorId)
                .doctor(doctor)
                .patientId(patientId)
                .patient(patient)
                .note("Notatka2")
                .dateTime(LocalDateTime.of(2023, 8, 16, 9, 10))
                .status("odbyta")
                .build();
        entityManager.persistAndFlush(visit1);
        entityManager.persistAndFlush(visit2);

        // when
        List<VisitDTO> result = visitRepository.findAllByDoctor(doctorId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(res -> doctorId.equals(res.getDoctorId()));
    }

    @Test
    void findAllByPatientWorksCorrectly() {
        PatientEntity patient = PatientEntity.builder()
                .name("Dominik")
                .surname("Bielski")
                .pesel("81101747896")
                .email("dominik.bielski@interia.pl")
                .phone("+48 123 444 777")
                .build();
        DoctorEntity doctor = DoctorEntity.builder()
                .name("Tadeusz")
                .surname("Ludwikowski")
                .title("Onkolog")
                .email("t.ludwikoski@eclinic.pl")
                .phone("+48 120 121 987")
                .build();

        Integer patientId = (Integer) entityManager.persistAndGetId(patient);
        Integer doctorId = (Integer) entityManager.persistAndGetId(doctor);

        VisitEntity visit1 = VisitEntity.builder()
                .doctorId(doctorId)
                .doctor(doctor)
                .patientId(patientId)
                .patient(patient)
                .note("Notatka1")
                .dateTime(LocalDateTime.of(2023, 8, 15, 9, 10))
                .status("odbyta")
                .build();
        VisitEntity visit2 = VisitEntity.builder()
                .doctorId(doctorId)
                .doctor(doctor)
                .patientId(patientId)
                .patient(patient)
                .note("Notatka2")
                .dateTime(LocalDateTime.of(2023, 8, 16, 9, 10))
                .status("odbyta")
                .build();
        entityManager.persistAndFlush(visit1);
        entityManager.persistAndFlush(visit2);

        // when
        List<VisitDTO> result = visitRepository.findAllByPatient(patientId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(res -> patientId.equals(res.getPatientId()));
    }

    @Test
    void findAllForBothWorksCorrectly() {
        DoctorEntity doctor = DoctorEntity.builder()
                .name("Tadeusz")
                .surname("Ludwikowski")
                .title("Onkolog")
                .email("t.ludwikoski@eclinic.pl")
                .phone("+48 120 121 987")
                .build();
        PatientEntity patient = PatientEntity.builder()
                .name("Dominik")
                .surname("Bielski")
                .pesel("81101747896")
                .email("dominik.bielski@interia.pl")
                .phone("+48 123 444 777")
                .build();

        Integer doctorId = (Integer) entityManager.persistAndGetId(doctor);
        Integer patientId = (Integer) entityManager.persistAndGetId(patient);

        VisitEntity visit1 = VisitEntity.builder()
                .doctorId(doctorId)
                .doctor(doctor)
                .patientId(patientId)
                .patient(patient)
                .note("Notatka1")
                .dateTime(LocalDateTime.of(2023, 8, 15, 9, 10))
                .status("odbyta")
                .build();
        VisitEntity visit2 = VisitEntity.builder()
                .doctorId(doctorId)
                .doctor(doctor)
                .patientId(patientId)
                .patient(patient)
                .note("Notatka2")
                .dateTime(LocalDateTime.of(2023, 8, 16, 9, 10))
                .status("odbyta")
                .build();
        entityManager.persistAndFlush(visit1);
        entityManager.persistAndFlush(visit2);

        // when
        List<VisitDTO> result = visitRepository.findAllForBoth(doctorId, patientId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(res -> doctorId.equals(res.getDoctorId())
                && patientId.equals(res.getPatientId()));

    }
}