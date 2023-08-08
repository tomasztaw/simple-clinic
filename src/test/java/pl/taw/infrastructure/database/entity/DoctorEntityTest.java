package pl.taw.infrastructure.database.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.taw.util.EntityFixtures;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class DoctorEntityTest {

    @Test
    public void testDoctorEntity() {
        // given
        DoctorEntity doctor = DoctorEntity.builder()
                .doctorId(1)
                .name("John")
                .surname("Doe")
                .title("MD")
                .email("john.doe@example.com")
                .phone("+123456789")
                .visits(Collections.emptyList())
                .schedules(Collections.emptyList())
                .opinions(Collections.emptyList())
                .build();
        DoctorEntity doctorEntity = EntityFixtures.someDoctor2();

        // when
        String name = doctor.getName();
        String surname = doctor.getSurname();

        // then
        assertNotNull(doctor.getDoctorId());
        assertEquals("John", name);
        assertEquals("Doe", surname);
    }

}