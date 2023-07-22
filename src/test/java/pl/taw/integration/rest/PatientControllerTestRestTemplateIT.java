package pl.taw.integration.rest;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import pl.taw.api.dto.PatientsDTO;
import pl.taw.integration.configuration.AbstractIT;
import pl.taw.util.DtoFixtures;

/**
 * Stary sposób testowania, dlatego tylko jeden przykładowy test.
 * Będziemy używać RestAssured
 * w21-28
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PatientControllerTestRestTemplateIT extends AbstractIT {

    @LocalServerPort
    private int port;

    private final TestRestTemplate testRestTemplate;

    @Test
    void thatPatientListWorksCorrectly() {
        // given
        String url = "http://localhost:%s/clinic/patients/all/dots".formatted(port);

        this.testRestTemplate.postForEntity(url, DtoFixtures.patients, PatientsDTO.class);

        // when
        ResponseEntity<PatientsDTO> result = this.testRestTemplate.getForEntity(url, PatientsDTO.class);
        PatientsDTO body = result.getBody();

        // then
        Assertions.assertThat(body).isNotNull();
        Assertions.assertThat(body.getPatients()).hasSizeGreaterThan(0);
    }
}
