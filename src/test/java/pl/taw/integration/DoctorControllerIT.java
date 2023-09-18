package pl.taw.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.taw.integration.configuration.AbstractIT;

import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(properties = "spring.security.enabled=false")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DoctorControllerIT extends AbstractIT {

    /**
     * Mam problemy, test teoretycznie przechodzi, ale bez adnotacji SBT
     */

    @LocalServerPort
    private int port;

    private final TestRestTemplate testRestTemplate; // udaje przeglądarkę


    @Test
    void applicationWorksCorrectlyOldView() {
        // given
        String url = "http://localhost:%s/clinic/doctors/all-old".formatted(port);

        // when
        String page = this.testRestTemplate.getForObject(url, String.class);

        // then
        assertThat(page).contains("<title>Lista lekarzy</title>");
    }

    @Test
    void applicationWorksCorrectlyNewView() {
        // given
        String url = "http://localhost:%s/clinic/doctors".formatted(port);

        // when
        String page = this.testRestTemplate.getForObject(url, String.class);

        // then
        assertThat(page).contains("<title>Lista Lekarzy</title>");
    }
}
