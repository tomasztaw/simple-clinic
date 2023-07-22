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
import pl.taw.infrastructure.database.repository.forpet.PetRepository;

//@Import(pl.taw.integration.configuration.PersistenceContainerTestConfiguration.class)
@ActiveProfiles("test")
@Import(PersistenceContainerTestConfiguration.class)
@SpringBootTest(properties = "spring.security.enabled=false", // po dodaniu tego test działa poprawnie !!!!
    classes = {KlinikaApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIT {

    @LocalServerPort
    protected int port;

    @Value("${server.servlet.context-path}")
    protected String basePath;

//    @AfterEach
//    void afterEach() {
//
//    }

    // w21-28 konfiguracja: ######################
    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PatientRepository patientRepository;

    @AfterEach
    void afterEach() {
        petRepository.deleteAll();
        // nie mam deleteAll dla patient repo
    }
}
