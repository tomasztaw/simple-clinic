package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.repository.jpa.DoctorJpaRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DoctorRepositoryDataJpaTest extends AbstractJpaIT {

    private DoctorRepository doctorRepository;
    private DoctorJpaRepository doctorJpaRepository;
    private TestEntityManager entityManager;

    @Test
    void thatDoctorsCanBySaveCorrectly() {
        // given
        DoctorEntity doctorEntity1 = DoctorEntity.builder()
                .name("Alicja")
                .surname("Kolankiewicz")
                .title("Pediatra")
                .email("ale.kolan@eclinic.pl")
                .phone("+48 120 130 401")
                .build();
        DoctorEntity doctorEntity2 = DoctorEntity.builder()
                .name("Karol")
                .surname("Nikczemny")
                .title("Onkolog")
                .email("k.nikczemny@eclinic.pl")
                .phone("+48 120 130 402")
                .build();

        List<DoctorEntity> doctorEntities = List.of(doctorEntity1, doctorEntity2);
        long expectedSize = doctorJpaRepository.count() + doctorEntities.size();

        // when
        doctorRepository.saveAllAndFlush(doctorEntities);
        List<DoctorDTO> doctorsFound = doctorRepository.findAll();

        // then
        Assertions.assertThat(doctorsFound.size()).isEqualTo(expectedSize);
        Assertions.assertThat(doctorsFound.get(doctorsFound.size()-1).getPhone()).isEqualTo(doctorEntity2.getPhone());
    }

    @Test
    void thatDoctorRepositoryFindAllWorksCorrectly() {
        // given
        long expectedSize = doctorJpaRepository.count();

        // when
        List<DoctorDTO> doctorsFromRepository = doctorRepository.findAll();

        // then
        assertEquals(expectedSize, doctorsFromRepository.size());
        assertTrue(doctorsFromRepository.stream().allMatch(doctor -> doctor.getEmail().endsWith("@eclinic.pl")));
    }

    @Test
    void thatDoctorRepositoryFindBySpecializationWorksCorrectly() {
        // given
        String kardiolog = "Kardiolog";
        long expectedSize = doctorRepository.findAll().stream().map(DoctorDTO::getTitle).filter(kardiolog::equalsIgnoreCase).count();

        // when
        List<DoctorDTO> doctorsWithCardio = doctorRepository.findBySpecialization(kardiolog);

        // then
        assertEquals(expectedSize, doctorsWithCardio.size());
        assertTrue(doctorsWithCardio.stream().allMatch(doctor -> doctor.getTitle().equalsIgnoreCase(kardiolog)));
    }

    @Test
    void thatDoctorRepositoryFindBySpecializationsWorksCorrectly() {
        // given
        long expectedSize = doctorRepository.findAll().stream().map(DoctorDTO::getTitle).distinct().count();
        List<String> expectedList = doctorRepository.findAll().stream().map(DoctorDTO::getTitle).distinct().toList();

        // when
        List<String> result = doctorRepository.findAllSpecializations();

        // then
        assertEquals(expectedSize, result.size());
        assertEquals(expectedList, result);
    }

    @Test
    void thatDoctorRepositoryFindByIdWorksCorrectly() {
        // given
        Integer doctorId = 5;
        DoctorDTO expectedDoctor = DoctorDTO.builder()
                .doctorId(doctorId)
                .name("Wacław")
                .surname("Piątkowski")
                .title("Gastrolog")
                .email("wacek@eclinic.pl")
                .phone("+48 120 130 150")
                .build();

        // when
        DoctorDTO result = doctorRepository.findById(doctorId);

        // then
        assertEquals(expectedDoctor, result);
    }

    @Test
    void testFindByIdNotFound() {
        // given
        Integer notExistingId = -128;

        // then
        assertThrows(NotFoundException.class,
                // when
                () -> doctorRepository.findById(notExistingId)
        );
    }

    @Test
    void thatDoctorRepositoryFindEntityByIdWorksCorrectly() {
        // given
        Integer doctorId = 5;
        DoctorEntity expectedDoctor = DoctorEntity.builder()
                .doctorId(doctorId)
                .name("Wacław")
                .surname("Piątkowski")
                .title("Gastrolog")
                .email("wacek@eclinic.pl")
                .phone("+48 120 130 150")
                .build();

        // when
        DoctorEntity result = doctorRepository.findEntityById(doctorId);

        // then
        assertEquals(expectedDoctor, result);
    }

    @Test
    void thatDoctorRepositorySaveAndReturnWorksCorrectly() {
        // given
        DoctorEntity expectedDoctor = DoctorEntity.builder()
                .name("Alicja")
                .surname("Niewiadomska")
                .title("Gastrolog")
                .email("ala.niewiadomska@eclinic.pl")
                .phone("+48 120 130 168")
                .build();

        // when
        DoctorEntity result = doctorRepository.saveAndReturn(expectedDoctor);

        // then
        assertNotNull(result.getDoctorId());
        assertEquals(expectedDoctor, result);
    }

    @Test
    void thatDoctorRepositorySaveWorksCorrectly() {
        // given
        DoctorEntity expectedDoctor = DoctorEntity.builder()
                .name("Roksana")
                .surname("Koniecpolska")
                .title("Pediatra")
                .email("roksa.koniec@eclinic.pl")
                .phone("+48 120 130 169")
                .build();

        // when
        doctorRepository.save(expectedDoctor);
        entityManager.flush();
        DoctorEntity savedDoctor = entityManager.find(DoctorEntity.class, expectedDoctor.getDoctorId());

        // then
        assertNotNull(expectedDoctor.getDoctorId());
        assertNotNull(savedDoctor);
        assertEquals(expectedDoctor.getName(), savedDoctor.getName());
        assertEquals(expectedDoctor.getTitle(), savedDoctor.getTitle());
    }

    @Test
    void thatDoctorRepositoryDeleteWorksCorrectly() {
        // given
        DoctorEntity doctorEntity = DoctorEntity.builder()
                .name("Zygfryd")
                .surname("Pawłowski")
                .title("Dermatolog")
                .email("zygi@eclinic.pl")
                .phone("+48 120 130 666")
                .build();
        entityManager.persistAndFlush(doctorEntity);

        // when
        doctorRepository.delete(doctorEntity);
        entityManager.flush();

        // then
        assertNull(entityManager.find(DoctorEntity.class, doctorEntity.getDoctorId()));
    }


}
