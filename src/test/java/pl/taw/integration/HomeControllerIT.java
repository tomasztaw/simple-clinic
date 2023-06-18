package pl.taw.integration;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import pl.taw.integration.configuration.AbstractIT;
//import pl.zajavka.integration.configuration.AbstractIT;

import static io.restassured.RestAssured.port;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HomeControllerIT extends AbstractIT {

    private final TestRestTemplate testRestTemplate;

    @Test
    void thatHomePageRequiredSigningIn() {
        String url = String.format("http://localhost:%s%s", port, basePath);

        String page = this.testRestTemplate.getForObject(url, String.class);
        Assertions.assertThat(page).contains("Please sign in");
    }

    @Test
    void thatMechanicPageRequiredSigningIn() {
        String url = String.format("http://localhost:%s%s/mechanic", port, basePath);

        String page = this.testRestTemplate.getForObject(url, String.class);
        Assertions.assertThat(page).contains("Please sign in");
    }
}
