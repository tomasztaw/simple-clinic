package pl.taw.integration.rest;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.PatientsDTO;
import pl.taw.integration.configuration.AbstractIT;

import java.util.List;

/**
 * Stary sposób testowania, dlatego tylko jeden przykładowy test.
 * Będziemy używać Rest Assured
 * w21-28
 * Url prowadzi pod dopisane metody dla testów, nie czyszczę bazy, nie robię 'postForEntity'
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PatientControllerTestRestTemplateIT extends AbstractIT {

    @LocalServerPort
    @SuppressWarnings("unused")
    private int port;

    private final TestRestTemplate testRestTemplate;

    @Test
    void thatPatientListWorksCorrectly() {
        // given
        String url = "http://localhost:%s/clinic/patients/all/dots".formatted(port);

        // when
        ResponseEntity<PatientsDTO> result = this.testRestTemplate.getForEntity(url, PatientsDTO.class);
        PatientsDTO body = result.getBody();

        // then
        Assertions.assertThat(body).isNotNull();
        Assertions.assertThat(body.getPatients()).hasSizeGreaterThan(0);
        Assertions.assertThat(body.getPatients()).hasSize(6);
    }

    @Test
    void thatPatientListWorksCorrectlyWithList() {
        // given
        String url = "http://localhost:%s/clinic/patients/all".formatted(port);

        // when
        ResponseEntity<List<PatientDTO>> result = this.testRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        List<PatientDTO> body = result.getBody();

        // then
        Assertions.assertThat(body).isNotNull();
        Assertions.assertThat(body).hasSizeGreaterThan(0);
        Assertions.assertThat(body).hasSize(6);
    }
}
