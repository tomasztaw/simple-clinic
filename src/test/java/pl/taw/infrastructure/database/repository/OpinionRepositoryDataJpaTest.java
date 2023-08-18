package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.api.dto.PatientDTO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.OpinionEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.config.AbstractJpaIT;
import pl.taw.infrastructure.database.repository.jpa.OpinionJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OpinionRepositoryDataJpaTest extends AbstractJpaIT {

    private OpinionRepository opinionRepository;
    private OpinionJpaRepository opinionJpaRepository;
    private TestEntityManager entityManager;

    @Test
    void testFindAllOpinionsReturnsCorrectSize() {
        // given
        long expectedSize = opinionJpaRepository.count();

        // when
        List<OpinionDTO> result = opinionRepository.findAll();

        // then
        assertThat(result.size()).isEqualTo(expectedSize);
        assertThat(result).hasSize((int) expectedSize);
    }

    @Test
    void testFindByIdWorksCorrectly() {
        // given
        Integer opinionId = 2;
        DoctorDTO doctor = DoctorDTO.builder()
                .doctorId(1)
                .name("Alojzy")
                .surname("Kowalski")
                .title("Laryngolog")
                .email("alojzkowalski@eclinic.pl")
                .phone("+48 120 121 122")
                .build();
        PatientDTO patient = PatientDTO.builder()
                .patientId(2)
                .name("Wojciech")
                .surname("Suchodolski")
                .pesel("72072514777")
                .email("suchy@gmail.com")
                .phone("+48 258 369 147")
                .build();
        OpinionDTO opinion = OpinionDTO.builder()
                .opinionId(opinionId)
                .doctorId(1)
                .doctor(doctor)
                .patientId(2)
                .patient(patient)
                .visitId(8)
                .comment("Wszystko super, polecam")
                .createdAt(LocalDateTime.of(2023, 6, 5, 13, 45))
                .build();

        // when
        OpinionDTO result = opinionRepository.findById(opinionId);

        // then
        assertThat(result).isEqualTo(opinion);
    }

    @Test
    void testFindByIdNotFound() {
        // given
        Integer notExistingId = -128;

        // then
        assertThrows(NotFoundException.class,
                // when
                () -> opinionRepository.findById(notExistingId)
        );
    }

    @Test
    void testFindEntityByIdWorksCorrectly() {
        // given
        Integer opinionId = 2;
        OpinionEntity opinion = OpinionEntity.builder()
                .opinionId(opinionId)
                .doctorId(1)
                .patientId(2)
                .visitId(8)
                .comment("Wszystko super, polecam")
                .createdAt(LocalDateTime.of(2023, 6, 5, 13, 45))
                .build();

        // when
        OpinionEntity result = opinionRepository.findEntityById(opinionId);

        // then
        assertThat(result.getOpinionId()).isEqualTo(opinionId);
        assertThat(result.getDoctorId()).isEqualTo(opinion.getDoctorId());
        assertThat(result.getCreatedAt()).isEqualTo(opinion.getCreatedAt());
    }

    @Test
    void testSaveAndReturnWorksCorrectly() {
        // given
        OpinionEntity opinion = OpinionEntity.builder()
                .doctorId(5)
                .patientId(5)
                .visitId(10)
                .comment("Wszystko na tip-top")
                .createdAt(LocalDateTime.of(2023, 8, 18, 12, 15))
                .build();

        // when
        OpinionEntity result = opinionRepository.saveAndReturn(opinion);

        // then
        assertNotNull(result.getOpinionId());
        assertEquals(opinion, result);
    }

    @Test
    void thatOpinionRepositorySaveWorksCorrectly() {
        // given
        OpinionEntity opinion = OpinionEntity.builder()
                .doctorId(4)
                .patientId(3)
                .visitId(9)
                .comment("Mogło być lepiej")
                .createdAt(LocalDateTime.of(2023, 8, 17, 10, 15))
                .build();

        // when
        opinionRepository.save(opinion);
        entityManager.flush();
        OpinionEntity savedOpinion = entityManager.find(OpinionEntity.class, opinion.getOpinionId());

        // then
        assertNotNull(opinion.getOpinionId());
        assertNotNull(savedOpinion);
        assertEquals(opinion.getComment(), savedOpinion.getComment());
        assertEquals(opinion.getCreatedAt(), savedOpinion.getCreatedAt());
    }


    @Test
    void thatOpinionRepositoryDeleteWorksCorrectly() {
        // given
        OpinionEntity opinion = OpinionEntity.builder()
                .doctorId(3)
                .patientId(2)
                .visitId(7)
                .comment("Wszystko do poprawy")
                .createdAt(LocalDateTime.of(2023, 8, 16, 13, 25))
                .build();
        entityManager.persistAndFlush(opinion);

        // when
        opinionRepository.delete(opinion);
        entityManager.flush();

        // then
        assertNull(entityManager.find(OpinionEntity.class, opinion.getOpinionId()));
    }

    @Test
    void testFindAllByDoctorWorksCorrectly() {
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

        OpinionEntity opinion1 = OpinionEntity.builder()
                .doctorId(doctorId)
                .doctor(doctor)
                .patientId(patientId)
                .patient(patient)
                .visitId(10)
                .comment("Opinion 1")
                .createdAt(LocalDateTime.of(2023, 8, 16, 13, 25))
                .build();
        OpinionEntity opinion2 = OpinionEntity.builder()
                .doctorId(doctorId)
                .doctor(doctor)
                .patientId(patientId)
                .patient(patient)
                .visitId(11)
                .comment("Opinion 2")
                .createdAt(LocalDateTime.of(2023, 8, 16, 13, 55))
                .build();
        entityManager.persist(opinion1);
        entityManager.persist(opinion2);
        entityManager.flush();

        // when
        List<OpinionDTO> opinions = opinionRepository.findAllByDoctor(doctorId);

        // then
        assertThat(opinions).hasSize(2);
        assertThat(opinions.get(0).getComment()).isEqualTo("Opinion 1");
        assertThat(opinions.get(1).getComment()).isEqualTo("Opinion 2");
        assertThat(opinions.get(0).getDoctorId()).isEqualTo(doctorId);
    }

    @Test
    void testFindAllByPatientWorksCorrectly() {
        // given
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

        OpinionEntity opinion1 = OpinionEntity.builder()
                .doctorId(doctorId)
                .doctor(doctor)
                .patientId(patientId)
                .patient(patient)
                .visitId(10)
                .comment("Opinion 1")
                .createdAt(LocalDateTime.of(2023, 8, 16, 13, 25))
                .build();
        OpinionEntity opinion2 = OpinionEntity.builder()
                .doctorId(doctorId)
                .doctor(doctor)
                .patientId(patientId)
                .patient(patient)
                .visitId(11)
                .comment("Opinion 2")
                .createdAt(LocalDateTime.of(2023, 8, 16, 13, 55))
                .build();
        entityManager.persist(opinion1);
        entityManager.persist(opinion2);
        entityManager.flush();

        // when
        List<OpinionDTO> opinions = opinionRepository.findAllByDoctor(doctorId);

        // then
        assertThat(opinions).hasSize(2);
        assertThat(opinions.get(0).getComment()).isEqualTo("Opinion 1");
        assertThat(opinions.get(1).getComment()).isEqualTo("Opinion 2");
        assertThat(opinions.get(0).getPatientId()).isEqualTo(patientId);
    }
}
