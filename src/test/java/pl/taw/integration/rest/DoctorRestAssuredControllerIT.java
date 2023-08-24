package pl.taw.integration.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.integration.configuration.AbstractIT;
import pl.taw.integration.configuration.TestSecurityConfig;
import pl.taw.util.DtoFixtures;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.taw.api.controller.rest.DoctorRestController.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class DoctorRestAssuredControllerIT extends AbstractIT {

    /**
     * Nie za bardzo rozumiem, dlaczego jest to klasa niedziedzicząca z RestAssuredIntegrationTestBase
     * Bez dziedziczenia z AbstractIT testy zapisywały dane do prawdziwej bazy
     * Nie wiem, z jakiego to materiału
     */

    @LocalServerPort
    @SuppressWarnings("unused")
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost:%s/clinic".formatted(port);
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
    }

    @Autowired
    @SuppressWarnings("unused")
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "testUser") // niepotrzebne dla wyłączonego security
    public void testShowDoctorByIdWithUser() throws Exception {
        int doctorId = 1;

        mockMvc.perform(get(API_DOCTORS.concat(DOCTOR_ID), doctorId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("doctorId").value(doctorId))
                .andExpect(jsonPath("email", endsWith("@eclinic.pl")))
                .andExpect(jsonPath("phone", startsWith("+48 120")));
    }

    @Test
    public void testShowDoctorDetails() {
        int doctorId = 5;

        given().
                pathParam("doctorId", doctorId)
                .when()
                .get(API_DOCTORS.concat(DOCTOR_ID))
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("doctorId", equalTo(doctorId))
                .body("phone", startsWith("+48 120"))
                .body("email", endsWith("@eclinic.pl"))
                .body("title", notNullValue());
    }

    @Test
    public void testShowDoctorDetails_InvalidDoctorId() {
        int invalidDoctorId = -1;

        given()
                .pathParam("doctorId", invalidDoctorId)
                .when()
                .get(API_DOCTORS.concat(DOCTOR_ID))
                .then()
                .statusCode(404);
    }

    @Test
    public void testShowDoctorById() {
//        int doctorId = 2;
        int doctorId = 7;

        given()
                .pathParam("doctorId", doctorId)
                .when()
                .get(API_DOCTORS.concat(DOCTOR_ID))
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("doctorId", equalTo(doctorId))
                .body("email", containsString("@eclinic.pl"));
    }

    @Test
    public void shouldReturnListOfDoctors() {
        given()
                .baseUri(baseURI)
                .port(port)
                .when()
                .get(API_DOCTORS)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("size()", greaterThan(0));
    }

    @Test
    public void anotherListOfDoctors() {
        given()
                .when()
                .get(API_DOCTORS)
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("size()", greaterThanOrEqualTo(1))
                .body("doctors.name", everyItem(notNullValue()))
                .body("doctors.surname", everyItem(notNullValue()))
                .body("doctors.title", everyItem(notNullValue()))
                .body("doctors.phone", everyItem(containsString("+48 120")))
                .body("doctors.email", everyItem(containsString("@eclinic.pl")));
    }

    @Test
    public void shouldReturnListOfDoctorsExtra() {
        given()
                .baseUri(baseURI)
                .port(port)
                .when()
                .get(API_DOCTORS)
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
    public void testShowHistoryEndpoint() {
        int doctorId = 3;

        given()
                .pathParam("doctorId", doctorId)
                .when()
                .get(API_DOCTORS.concat(HISTORY))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("visits.doctor.doctorId", everyItem(equalTo(doctorId)))
                .body("size()", greaterThan(0));
    }

    @Test
    public void testShowHistory_DoctorNotFound() {
        int doctorId = -99;

        given()
                .pathParam("doctorId", doctorId)
                .when()
                .get(API_DOCTORS.concat(HISTORY))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testAddDoctor() {
        String requestBody = "{\"name\":\"Adam\",\"surname\":\"Nowiczok\",\"title\":\"Dermatolog\"," +
                        "\"phone\":\"+48 120 456 333\",\"email\":\"a.nowiczok@eclinic.pl\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(API_DOCTORS)
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void testAddDoctorWithJsonMap() {
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("name", "Józef");
        jsonMap.put("surname", "Kałużyn");
        jsonMap.put("title", "Podolog");
        jsonMap.put("phone", "+48 120 020 222");
        jsonMap.put("email", "j.kal@eclinic.pl");

        given()
                .contentType(ContentType.JSON)
                .body(jsonMap)
                .when()
                .post(API_DOCTORS)
                .then()
                .statusCode(201)
                .header("Location", matchesPattern(API_DOCTORS + "/\\d+"))
                .header("Location", containsString(API_DOCTORS));
    }

    @Test
    public void testUpdateDoctorNewOne() {
        DoctorDTO doctorDTO = DtoFixtures.someDoctor3();

        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("name", "Tomasz");
        jsonMap.put("surname", "Piasecki");
        jsonMap.put("title", "Psychiatra");
        jsonMap.put("phone", "+48 120 100 002");
        jsonMap.put("email", "tomasz.piasecki@eclinic.pl");

        given()
                .contentType(ContentType.JSON)
                .body(jsonMap)
                .when()
                .put(API_DOCTORS.concat(DOCTOR_ID), doctorDTO.getDoctorId())
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testDeleteExistingDoctor() {
        int doctorId = 4;

        given()
                .pathParam("doctorId", doctorId)
                .when()
                .delete(API_DOCTORS.concat(DOCTOR_ID))
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void testDeleteNonExistingDoctor() {
        int doctorId = -999;

        given()
                .pathParam("doctorId", doctorId)
                .when()
                .delete(API_DOCTORS.concat(DOCTOR_ID))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testUpdateDoctorTitle_Success() {
        int doctorId = 1;
        String newTitle = "Nowy tytuł";

        given()
                .pathParam("doctorId", doctorId)
                .param("newTitle", newTitle)
                .when()
                .patch(API_DOCTORS.concat(DOCTOR_UPDATE_TITLE))
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testUpdateDoctorTitle_NonExistingDoctor() {
        int doctorId = -999;

        given()
                .pathParam("doctorId", doctorId)
                .param("newTitle", "Nowy tytuł")
                .when()
                .patch(API_DOCTORS.concat(DOCTOR_UPDATE_TITLE))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testUpdateDoctorPhone_Success() {
        int doctorId = 1;
        String newPhone = "+48 120 111 228";

        given()
                .pathParam("doctorId", doctorId)
                .param("newPhone", newPhone)
                .when()
                .patch(API_DOCTORS.concat(DOCTOR_UPDATE_PHONE))
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testUpdateDoctorPhone_NonExistingDoctor() {
        int doctorId = -999;

        given()
                .pathParam("doctorId", doctorId)
                .param("newPhone", "+48 120 111 228")
                .when()
                .patch(API_DOCTORS.concat(DOCTOR_UPDATE_PHONE))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testUpdateDoctorEmail_Success() {
        int doctorId = 1;
        String newEmail = "jakis.email@eclinic.pl";

        given()
                .pathParam("doctorId", doctorId)
                .param("newEmail", newEmail)
                .when()
                .patch(API_DOCTORS.concat(DOCTOR_UPDATE_EMAIL))
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testUpdateDoctorEmail_NonExistingDoctor() {
        int doctorId = -999;

        given()
                .pathParam("doctorId", doctorId)
                .param("newEmail", "jakis.email@eclinic.pl")
                .when()
                .patch(API_DOCTORS.concat(DOCTOR_UPDATE_EMAIL))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

}