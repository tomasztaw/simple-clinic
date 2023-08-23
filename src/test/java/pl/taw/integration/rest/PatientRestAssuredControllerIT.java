package pl.taw.integration.rest;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import pl.taw.api.dto.PatientDTO;
import pl.taw.integration.configuration.AbstractIT;
import pl.taw.integration.configuration.TestSecurityConfig;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static pl.taw.api.controller.rest.PatientRestController.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class PatientRestAssuredControllerIT extends AbstractIT {

    @LocalServerPort
    @SuppressWarnings("unused")
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost:%s/clinic".formatted(port);
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
    }

    @Test
    void testGetPatientsShouldWorksCorrectly() {
        given()
                .when()
                .get(API_PATIENTS)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0));
    }

    @Test
    public void anotherListOfPatients() {
        given()
                .when()
                .get(API_PATIENTS)
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("size()", greaterThanOrEqualTo(1))
                .body("patients.name", everyItem(notNullValue()))
                .body("patients.surname", everyItem(notNullValue()))
                .body("patients.pesel", everyItem(notNullValue()))
                .body("patients.phone", everyItem(containsString("+48")))
                .body("patients.email", everyItem(containsString("@")));
    }

    @Test
    public void testShowPatientById() {
        int patientId = 2;

        given()
                .pathParam("patientId", patientId)
                .when()
                .get(API_PATIENTS.concat(PATIENT_ID))
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("patientId", equalTo(patientId))
                .body("phone", startsWith("+48"))
                .body("email", containsString("@"));
    }

    @Test
    public void testShowPatientDetails_InvalidPatientId() {
        int invalidPatientId = -1;

        given()
                .pathParam("patientId", invalidPatientId)
                .when()
                .get(API_PATIENTS.concat(PATIENT_ID))
                .then()
                .statusCode(404);
    }

    @Test
    public void testShowPatientHistoryEndpoint() {
        int patientId = 3;

        given()
                .pathParam("patientId", patientId)
                .when()
                .get(API_PATIENTS.concat(HISTORY))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("visits.patient.patientId", everyItem(equalTo(patientId)))
                .body("size()", greaterThan(0));
    }

    @Test
    public void testShowHistory_PatientNotFound() {
        int patientId = -99;

        given()
                .pathParam("patientId", patientId)
                .when()
                .get(API_PATIENTS.concat(HISTORY))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testAddPatient_Success() {
        String name = "Jan";
        String surname = "Kowalski";
        String pesel = "68050545698";
        String phone = "+48 123 777 787";
        String email = "jan.kowalski@example.com";

        given()
                .param("name", name)
                .param("surname", surname)
                .param("pesel", pesel)
                .param("phone", phone)
                .param("email", email)
                .when()
                .post(API_PATIENTS)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("Dodano pacjenta: " + name + " " + surname));
    }

    @Test
    public void testUpdatePatient_Success() {
        int patientId = 3;
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("name", "Nowe-imię");
        jsonMap.put("surname", "Nowe-Nazwisko");
        jsonMap.put("pesel", "88123114789");
        jsonMap.put("phone", "+48 111 777 774");
        jsonMap.put("email", "nowy.email@example.com");

        given()
                .pathParam("patientId", patientId)
                .params(jsonMap)
                .when()
                .put(API_PATIENTS.concat(PATIENT_ID))
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("Aktualizacja wykonana"));
    }

    @Test
    public void testUpdatePatient_NonExistingPatient() {
        int patientId = -999;
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("name", "Nowe-imię");
        jsonMap.put("surname", "Nowe-Nazwisko");
        jsonMap.put("pesel", "88123114789");
        jsonMap.put("phone", "+48 111 777 774");
        jsonMap.put("email", "nowy.email@example.com");

        given()
                .pathParam("patientId", patientId)
                .params(jsonMap)
                .when()
                .put(API_PATIENTS.concat(PATIENT_ID))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testDeletePatient_Success() {
        int patientId = 5;

        given()
                .pathParam("patientId", patientId)
                .when()
                .delete(API_PATIENTS.concat(PATIENT_ID))
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void testDeletePatient_NonExistingPatient() {
        int patientId = -88;

        given()
                .pathParam("patientId", patientId)
                .when()
                .delete(API_PATIENTS.concat(PATIENT_ID))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testUpdatePatientPhone_Success() {
        int patientId = 2;
        PatientDTO patient = given().when().get(API_PATIENTS.concat(PATIENT_ID), patientId).as(PatientDTO.class);
        String newPhone = "+48 987 654 321";

        given()
                .pathParam("patientId", patientId)
                .param("newPhone", newPhone)
                .when()
                .patch(API_PATIENTS.concat(PATIENT_UPDATE_PHONE))
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("Numer zaktualizowany na [" + newPhone + "] " +
                        "dla pacjenta [" + patient.getName() + " " + patient.getSurname() + "]"));
    }

    @Test
    public void testUpdatePatientPhone_NonExistingPatient() {
        int patientId = -9;

        given()
                .pathParam("patientId", patientId)
                .param("newPhone", "+48 987 654 321")
                .when()
                .patch(API_PATIENTS.concat(PATIENT_UPDATE_PHONE))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testUpdatePatientEmail_Success() {
        int patientId = 1;
        PatientDTO patient = given().when().get(API_PATIENTS.concat(PATIENT_ID), patientId).as(PatientDTO.class);
        String newEmail = "nowy.email@wp.pl";

        given()
                .pathParam("patientId", patientId)
                .param("newEmail", newEmail)
                .when()
                .patch(API_PATIENTS.concat(PATIENT_UPDATE_EMAIL))
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("Email zaktualizowany na [" + newEmail + "] " +
                        "dla pacjenta [" + patient.getName() + " " + patient.getSurname() + "]"));
    }

    @Test
    public void testUpdatePatientEmail_NonExistingPatient() {
        int patientId = -9;

        given()
                .pathParam("patientId", patientId)
                .param("newEmail", "nowy.email@wp.pl")
                .when()
                .patch(API_PATIENTS.concat(PATIENT_UPDATE_EMAIL))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

}
