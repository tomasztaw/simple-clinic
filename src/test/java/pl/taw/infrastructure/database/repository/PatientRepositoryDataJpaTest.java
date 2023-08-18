package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.taw.api.dto.PatientDTO;
import pl.taw.domain.exception.NotFoundException;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.config.AbstractJpaIT;
import pl.taw.infrastructure.database.repository.jpa.PatientJpaRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Materiał - w20-11 @DataJpaTest
 * 16-08-2023 r.
 */

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PatientRepositoryDataJpaTest extends AbstractJpaIT {

    private PatientRepository patientRepository;

    private PatientJpaRepository patientJpaRepository;
    private TestEntityManager entityManager;

    @Test
    void testFindAllPatientsReturnsCorrectSize() {
        // given
        long expectedSize = patientJpaRepository.count();

        // when
        List<PatientDTO> result = patientRepository.findAll();

        // then
        assertThat(result.size()).isEqualTo(expectedSize);
        assertThat(result).hasSize((int) expectedSize);
    }

    @Test
    void testPatientRepositoryFindByIdWorksCorrectly() {
        // given
        PatientEntity patient = PatientEntity.builder()
                .name("Stefan")
                .surname("Niewiadomy")
                .pesel("77022545698")
                .email("s.niew@wp.pl")
                .phone("+48 147 789 357")
                .build();
        Integer patientId = (Integer) entityManager.persistAndGetId(patient);

        // when
        PatientDTO foundPatient = patientRepository.findById(patientId);

        // then
        Assertions.assertThat(foundPatient.getPatientId()).isEqualTo(patientId);
        Assertions.assertThat(foundPatient.getPesel()).isEqualTo(patient.getPesel());
        Assertions.assertThat(foundPatient.getEmail()).isEqualTo(patient.getEmail());
    }

    @Test
    void testPatientRepositoryFindEntityByIdWorksCorrectly() {
        // given
        PatientEntity patient = PatientEntity.builder()
                .name("Stefania")
                .surname("Niewiadomska")
                .pesel("77081245677")
                .email("s.niewiadomska@wp.pl")
                .phone("+48 147 789 887")
                .build();
        Integer patientId = (Integer) entityManager.persistAndGetId(patient);

        // when
        PatientEntity foundPatient = patientRepository.findEntityById(patientId);

        // then
        Assertions.assertThat(foundPatient.getPatientId()).isEqualTo(patientId);
        Assertions.assertThat(foundPatient.getPesel()).isEqualTo(patient.getPesel());
        Assertions.assertThat(foundPatient.getEmail()).isEqualTo(patient.getEmail());
    }

    @Test
    void testFindByPeselWorksCorrectlyWithExistingSource() {
        // given
        String pesel = "72072514725";

        // when
        PatientDTO result = patientRepository.findByPesel(pesel);

        // then
        assertThat(result.getPesel()).isEqualTo(pesel);
    }

    @Test
    void testFindByPeselShouldThrowWithNoExistingSource() {
        // given
        String pesel = "72072514000";

        // then
        org.junit.jupiter.api.Assertions.assertThrows(NotFoundException.class,
                // when
                () -> patientRepository.findByPesel(pesel)
        );
    }    @Test

    void testFindByEmailWorksCorrectlyWithExistingSource() {
        // given
        String email = "aa@gmail.com";

        // when
        PatientDTO result = patientRepository.findByEmail(email);

        // then
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    void testFindByEmailShouldThrowWithNoExistingSource() {
        // given
        String email = "aabc@gmail.com";

        // then
        org.junit.jupiter.api.Assertions.assertThrows(NotFoundException.class,
                // when
                () -> patientRepository.findByEmail(email)
        );
    }

    @Test
    void thatPatientRepositorySaveAndReturnWorksCorrectly() {
        // given
        PatientEntity expectedPatient = PatientEntity.builder()
                .name("Ala")
                .surname("Kołodziejska")
                .pesel("75112836987")
                .email("ala.kolo@onet.pl")
                .phone("+48 441 557 697")
                .build();

        // when
        PatientEntity result = patientRepository.saveAndReturn(expectedPatient);

        // then
        assertNotNull(result.getPatientId());
        assertEquals(expectedPatient, result);
    }

    @Test
    void testSaveAndReturnShouldThrowWithExistingEmail() {
        // given
        String email = "aa@gmail.com";

        PatientEntity patientForSave = PatientEntity.builder()
                .name("Ala")
                .surname("Kołodziejska")
                .pesel("75112836987")
                .email(email)
                .phone("+48 441 557 697")
                .build();

        // then
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                // when
                () -> patientRepository.saveAndReturn(patientForSave)
        );
    }

    @Test
    void testSaveAndReturnShouldThrowWithExistingEmailCheckMessage() {
        // given
        String email = "aa@gmail.com";

        PatientEntity patientForSave = PatientEntity.builder()
                .name("Ala")
                .surname("Kołodziejska")
                .pesel("75112836987")
                .email(email)
                .phone("+48 441 557 697")
                .build();

        // when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> patientRepository.saveAndReturn(patientForSave));

        // then
        assertThat(exception.getMessage()).contains("W systemie znajduje się już ktoś z takim emailem");
    }

    @Test
    void thatPatientRepositorySaveWorksCorrectly() {
        // given
        PatientEntity expectedPatient = PatientEntity.builder()
                .name("Filip")
                .surname("Rozwadowski")
                .pesel("72121425897")
                .email("fifi@wp.pl")
                .phone("+48 887 459 668")
                .build();

        // when
        patientRepository.save(expectedPatient);
        entityManager.flush();
        PatientEntity savedPatient = entityManager.find(PatientEntity.class, expectedPatient.getPatientId());

        // then
        assertNotNull(expectedPatient.getPatientId());
        assertNotNull(savedPatient);
        assertEquals(expectedPatient.getName(), savedPatient.getName());
        assertEquals(expectedPatient.getPesel(), savedPatient.getPesel());
    }

    @Test
    void testSaveShouldThrowWithExistingEmailCheckMessage() {
        // given
        String email = "aa@gmail.com";

        PatientEntity patientForSave = PatientEntity.builder()
                .name("Ala")
                .surname("Kołodziejska")
                .pesel("75112836987")
                .email(email)
                .phone("+48 441 557 697")
                .build();

        // when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> patientRepository.save(patientForSave));

        // then
        assertThat(exception.getMessage()).contains("W systemie znajduje się już ktoś z takim peselem/emailem");
    }

    @Test
    void thatPatientRepositoryDeleteWorksCorrectly() {
        // given
        PatientEntity patient = PatientEntity.builder()
                .name("Kornel")
                .surname("Czyżowicz")
                .pesel("81031923567")
                .email("kornel@jakis.pl")
                .phone("+48 225 888 745")
                .build();
        entityManager.persistAndFlush(patient);

        // when
        patientRepository.delete(patient);
        entityManager.flush();

        // then
        assertNull(entityManager.find(PatientEntity.class, patient.getPatientId()));
    }
}
