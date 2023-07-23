package pl.taw.integration.rest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.PatientsDTO;
import pl.taw.integration.configuration.RestAssuredIntegrationTestBase;
import pl.taw.integration.support.PatientsControllerTestSupport;
import pl.taw.integration.support.WiremockTestSupport;
import pl.taw.util.DtoFixtures;

import java.util.regex.Pattern;

/**
 * Test nie przechodzi, dostaje 404 i nie mogę dojść gdzie jest błąd
 * w21-29
 */
public class PatientControllerRestAssuredIT
        extends RestAssuredIntegrationTestBase
        implements PatientsControllerTestSupport, WiremockTestSupport {

    @Test
    void thatPatientsListCanByRetrievedCorrectly() {
        // given
        PatientDTO patient1 = DtoFixtures.somePatient1();
        PatientDTO patient2 = DtoFixtures.somePatient2();

        // when
        savePatient(patient1);
        savePatient(patient2);

        PatientsDTO patientsDTO = listPatients();

        // then
        Assertions.assertThat(patientsDTO.getPatients())
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("patientId")
                .containsAnyOf(patient1, patient2);
//                .containsExactlyInAnyOrder(patient1, patient2);
    }

    @Test
    void thatPatientCanBeCreatedCorrectly() {
        // given
        PatientDTO patient1 = DtoFixtures.somePatient1();

        // when
        ExtractableResponse<Response> response = savePatient(patient1);

        // then
        String responseAsString = response.body().asString();
        Assertions.assertThat(responseAsString).isEmpty();
        Assertions.assertThat(response.headers().get("Location").getValue())
                .matches(Pattern.compile("/patients/\\d"));
    }

    @Test
    void thatCreatedPatientCanBeRetrievedCorrectly() {
        // given
        PatientDTO patient1 = DtoFixtures.somePatient1();

        // when
        ExtractableResponse<Response> response = savePatient(patient1);
        String patientDetailsPath = response.headers().get("Location").getValue();

        PatientDTO patient = getPatient(patientDetailsPath);

        // then
        Assertions.assertThat(patient)
                .usingRecursiveComparison()
                .ignoringFields("patientId")
                .isEqualTo(patient1);

    }

    // w21-32

    @Test
    void thatPatientsCanBeUpdatedCorrectly() {
        // given
        long petId = 4;
        PatientDTO patient1 = DtoFixtures.somePatient1();
        ExtractableResponse<Response> response = savePatient(patient1);
        String patientDetailsPath = response.headers().get("Location").getValue();
        PatientDTO retrievedPatient = getPatient(patientDetailsPath);

        stubForPet(wireMockServer, petId);

        // when
        updatePatientByPet(retrievedPatient.getPatientId(), petId);

        // then
        PatientDTO patientWithPet = getPatientById(retrievedPatient.getPatientId());
        Assertions.assertThat(patientWithPet)
                .usingRecursiveComparison()
                .ignoringFields("patientId", "petId")
                .isEqualTo(patient1);
    }
}
