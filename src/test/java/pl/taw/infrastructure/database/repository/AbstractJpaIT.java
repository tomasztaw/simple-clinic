package pl.taw.infrastructure.database.repository;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * Generyczne wykorzystanie dla wielu testów @DataJpaTest
 * */

//@DataJpaTest(properties = "spring.security.enabled=false")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // oznacza, że nie chcemy korzystać z wbudowanej bazy H2
@TestPropertySource(locations = "classpath:application-test.yml")
@ContextConfiguration(initializers = DatabaseContainerInitializer.class)
public abstract class AbstractJpaIT {

}
