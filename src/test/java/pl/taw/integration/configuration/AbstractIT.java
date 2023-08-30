package pl.taw.integration.configuration;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import pl.taw.KlinikaApplication;
import pl.taw.infrastructure.database.repository.PatientRepository;

@ActiveProfiles("test")
@Import(PersistenceContainerTestConfiguration.class)
@SpringBootTest(
        properties = "spring.security.enabled=false",
        classes = {KlinikaApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIT {

    /**
     * Konfiguracja dla testów integracyjnych całej aplikacji w21-27
     *
     */

    @LocalServerPort
    protected int port;

    @Value("${server.servlet.context-path}")
    protected String basePath;

    @Autowired
    private PatientRepository patientRepository;



    @AfterEach
    void afterEach() {
        // czyszczenie bazy po każdym teście
        // nie mam deleteAll dla patient repo

    }
}
