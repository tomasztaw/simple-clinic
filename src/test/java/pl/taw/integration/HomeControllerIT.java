package pl.taw.integration;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import pl.taw.integration.configuration.AbstractIT;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HomeControllerIT extends AbstractIT {

    private final TestRestTemplate testRestTemplate;

    @Test
    void thatHomePageWorksCorrectly() {
        // given
        String url = String.format("http://localhost:%s%s", port, basePath);

        // when
        String page = this.testRestTemplate.getForObject(url, String.class);

        // then
        Assertions.assertThat(page).contains("Strona główna");
    }

    @Test
    void thatDoctorsPageWorksCorrectly() {
        // given
        String url = String.format("http://localhost:%s%s/doctors", port, basePath);

        // when
        String page = this.testRestTemplate.getForObject(url, String.class);

        // then
        Assertions.assertThat(page).contains("Lista lekarzy");
    }
}
