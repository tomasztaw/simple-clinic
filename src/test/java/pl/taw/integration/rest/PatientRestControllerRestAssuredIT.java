package pl.taw.integration.rest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import pl.taw.api.controller.rest.PatientRestController;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.PatientsDTO;
import pl.taw.integration.configuration.RestAssuredIntegrationTestBase;
import pl.taw.integration.configuration.TestSecurityConfig;
import pl.taw.integration.support.PatientsControllerTestSupport;
import pl.taw.integration.support.WiremockTestSupport;
import pl.taw.util.DtoFixtures;

import java.util.regex.Pattern;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Test nie przechodzi, dostaje 404 i nie mogę dojść, gdzie jest błąd
 * w21-29
 * Są problemy z autoryzacją, poczekam jeszcze czy będzie jak to obejść przy włączonym security
 */
public class PatientRestControllerRestAssuredIT
        extends RestAssuredIntegrationTestBase
        implements PatientsControllerTestSupport, WiremockTestSupport {

    @Test
    public void testShowPatientById() {
        baseURI = "http://localhost:9999/clinic";
//        baseURI = "http://localhost:8080/clinic";

        int patientId = 1;

        given()
                .pathParam("patientId", patientId)
                .when()
                .get(PatientRestController.API_PATIENTS.concat(PatientRestController.PATIENT_ID))
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(patientId));
    }

    @Test
    public void testGetPatients() {
        // Ustawienie bazowego URL do twojego API
//        baseURI = "http://localhost:%s%s".formatted(port, basePath);
//        baseURI = "http://localhost:8080/clinic";
        baseURI = "http://localhost:%s/clinic".formatted(port);

//        given().auth().preemptive().basic("tomek", "test")
        given()
                .when()
                .get(PatientRestController.API_PATIENTS)
                .then()
                .statusCode(200)
                .contentType("application/json");
//                .body("size()", greaterThanOrEqualTo(0));
    }

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
                .containsAnyOf(patient1, patient2)
                .containsExactlyInAnyOrder(patient1, patient2);
    }

    @Test
    void thatPatientsListCanByRetrievedCorrectlyNoAuto() {
        // given
        PatientDTO patient1 = DtoFixtures.somePatient1();
        PatientDTO patient2 = DtoFixtures.somePatient2();

        // when
        savePatient(patient1);
        savePatient(patient2);

        PatientsDTO patientsDTO = listPatientsNoAuto();

        // then
        Assertions.assertThat(patientsDTO.getPatients())
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("patientId")
                .containsAnyOf(patient1, patient2)
                .containsExactlyInAnyOrder(patient1, patient2);
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
    void testForAir() {
        // given
        Integer stationId = 117;

        stubForAir(wireMockServer, stationId);
    }

//    @Test
//    void thatPatientsCanBeUpdatedCorrectly() {
//        // given
//        long petId = 4;
//        PatientDTO patient1 = DtoFixtures.somePatient1();
//        ExtractableResponse<Response> response = savePatient(patient1);
//        String patientDetailsPath = response.headers().get("Location").getValue();
//        PatientDTO retrievedPatient = getPatient(patientDetailsPath);
//
//        stubForPet(wireMockServer, petId);
//
//        // when
//        updatePatientByPet(retrievedPatient.getPatientId(), petId);
//
//        // then
//        PatientDTO patientWithPet = getPatientById(retrievedPatient.getPatientId());
//        Assertions.assertThat(patientWithPet)
//                .usingRecursiveComparison()
//                .ignoringFields("patientId", "petId")
//                .isEqualTo(patient1);
//    }
}
