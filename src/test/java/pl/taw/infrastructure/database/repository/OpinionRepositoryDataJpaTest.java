package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.OpinionEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.util.EntityFixtures;

import java.util.List;

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OpinionRepositoryDataJpaTest extends AbstractJpaIT {

    private OpinionRepository opinionRepository;

    @Test
    void should() {
        DoctorEntity doctor = EntityFixtures.someDoctor1();
        PatientEntity patient = EntityFixtures.somePatient2();
        List<OpinionEntity> opinions = List.of(
                EntityFixtures.someOpinion2().withDoctorId(doctor.getDoctorId()).withDoctor(doctor),
                EntityFixtures.someOpinion1().withPatientId(patient.getPatientId()).withPatient(patient));

        opinions.forEach(opinion -> opinionRepository.save(opinion));

        List<OpinionDTO> result = opinionRepository.findAll();

        Assertions.assertThat(result).isEqualTo(2);

    }
}
