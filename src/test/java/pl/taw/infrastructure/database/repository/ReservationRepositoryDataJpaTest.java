package pl.taw.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.infrastructure.database.entity.ReservationEntity;
import pl.taw.util.EntityFixtures;

import java.util.List;

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ReservationRepositoryDataJpaTest extends AbstractJpaIT {

    private ReservationRepository reservationRepository;

    @Test
    void should() {
        ReservationEntity reservation = EntityFixtures.someReservation1();

        reservationRepository.save(reservation);

        List<ReservationDTO> all = reservationRepository.findAll();

        Assertions.assertThat(all.size()).isEqualTo(1);

    }

    @Test
    void should2() {
        List<ReservationEntity> reservationEntities = EntityFixtures.someReservationList;
        reservationEntities.forEach(res -> reservationRepository.save(res));

        List<ReservationDTO> result = reservationRepository.findAll();

        Assertions.assertThat(reservationEntities.size()).isEqualTo(result.size());

    }
}
