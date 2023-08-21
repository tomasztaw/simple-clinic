package pl.taw.proby;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import pl.taw.api.controller.rest.DoctorRestController;
import pl.taw.api.controller.rest.PatientRestController;
import pl.taw.integration.configuration.TestSecurityConfig;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.emptyOrNullString;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class ProbyRSIT {

    /**
     * Próby wykonane po nie udanych konfiguracjach dla w21-29
     */


    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost:%s/clinic".formatted(port);
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
    }


    @Test
    void podstawa() {
        given()
                .when()
//                .get("http://localhost:8080/clinic")
                .get(baseURI)
                .then()
                .statusCode(200);
    }

    @Test
    void listaLekarzy() {
        given()
                .when()
//                .get("http://localhost:8080/clinic/api/doctors")
                .get(baseURI + DoctorRestController.API_DOCTORS)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("size()", greaterThan(0))
                .body("doctors[0].name", not(emptyOrNullString()))
                .body("doctors[0].name", notNullValue())
                .body("doctors[0].email", containsString("@eclinic.pl"))
                .body("doctors[0].phone", startsWith("+48 120"))
                .body("doctors[0].title", not(emptyOrNullString()));
    }

    @Test
    public void testShowPatientById() {
//        baseURI = "http://localhost:9999/clinic";
//        baseURI = "http://localhost:8080/clinic";

        int patientId = 1;

        given()
                .pathParam("patientId", patientId)
                .when()
                .get(PatientRestController.API_PATIENTS.concat(PatientRestController.PATIENT_ID))
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("patientId", equalTo(patientId));
    }

    @Test
    public void testGetPatients() {
//        baseURI = "http://localhost:%s/clinic".formatted(port);

        given()
                .when()
                .get(PatientRestController.API_PATIENTS)
                .then()
                .statusCode(200)
                .contentType("application/json");
//                .body("size()", greaterThanOrEqualTo(0));
    }
}
