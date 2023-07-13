package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.util.EntityFixtures;

import java.util.List;

//@RunWith(SpringRunner.class) @ExtendWith(SpringExtension.class)
//@ExtendWith(SpringExtension.class)
//@AllArgsConstructor(onConstructor = @__(@Autowired))
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DoctorRepositoryDataJpaTest extends AbstractJpaIT {


    private final DoctorRepository doctorRepository;

//    @Test
//    void thatDoctorCanBySaveCorrectly() {
//        // given
//        List<DoctorEntity> doctorEntities = List.of(
//                EntityFixtures.someDoctor1(), EntityFixtures.someDoctor2(), EntityFixtures.someDoctor3());
//        doctorRepository.saveAllAndFlush(doctorEntities);
//
//        // when
//        List<DoctorDTO> doctorsFound = doctorRepository.findAll();
//
//        // then
//        Assertions.assertThat(doctorsFound.size()).isEqualTo(3);
//    }
}
