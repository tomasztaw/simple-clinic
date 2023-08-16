package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.taw.api.dto.PatientDTO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.jpa.PatientJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.PatientMapper;
import pl.taw.util.EntityFixtures;

import java.util.List;

/**
 * Materiał - w20-11 @DataJpaTest
 * 16-08-2023 r.
 */

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PatientRepositoryDataJpaTest extends AbstractJpaIT {

    private PatientRepository patientRepository;

//    @MockBean
//    private PatientJpaRepository patientJpaRepository;
//    @MockBean
//    private PatientMapper patientMapper;

    @Test
    void should() {
        PatientEntity patient = EntityFixtures.somePatient1().withPesel("881212123456").withEmail("email1@email.com");

        patientRepository.save(patient);

        List<PatientDTO> all = patientRepository.findAll();

        Assertions.assertThat(all.size()).isEqualTo(1);
    }

    @Test
    void thatPatientCanBeSavedCorrectly() {
        // given
        List<PatientEntity> patients = EntityFixtures.somePatientList;
        patients.forEach(patient -> patientRepository.save(patient));
//        patients.forEach(patient -> patientRepository.saveAndReturn(patient));

        // when
        List<PatientDTO> patientsFound = patientRepository.findAll();

        // then
        Assertions.assertThat(patientsFound.size()).isEqualTo(patients.size());
    }
}
