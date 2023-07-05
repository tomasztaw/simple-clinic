package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.infrastructure.database.repository.jpa.ReservationJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.ReservationMapper;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReservationRepositoryTest {

    @InjectMocks
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationJpaRepository reservationJpaRepository;
    @Mock
    private ReservationMapper reservationMapper;


    @Test
    void findAll() {
    }

    @Test
    void findById() {
    }

    @Test
    void findEntityById() {
    }

    @Test
    void saveAndReturn() {
    }

    @Test
    void save() {
    }

    @Test
    void delete() {
    }

    @Test
    void findAllByDoctor() {
    }

    @Test
    void findAllByPatient() {
    }

    @Test
    void findAllByDay() {
    }

    @Test
    void findAllByBoth() {
    }
}