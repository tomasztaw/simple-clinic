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
import pl.taw.api.dto.VisitDTO;
import pl.taw.integration.configuration.AbstractIT;
import pl.taw.integration.configuration.TestSecurityConfig;
import pl.taw.util.DtoFixtures;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
//import static pl.taw.api.controller.rest.VisitRestController.API_VISITS;
//import static pl.taw.api.controller.rest.VisitRestController.VISIT_ID;
import static pl.taw.api.controller.rest.VisitRestController.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class VisitRestAssuredControllerIT extends AbstractIT {


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
    void thatGetAllVisitWorksCorrectly() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(API_VISITS)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("size()", greaterThan(0))
                .body("visits.visitId", everyItem(isA(Integer.class)))
                .body("visits.note", everyItem(is(String.class)));
    }

    @Test
    void thatVisitDetailsWorksCorrectly() {
        int visitId = 1;

        given()
                .pathParam("visitId", visitId)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/visits/{visitId}")
//                .get(API_VISITS.concat(VISIT_ID))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
//                .body("visitId", equalTo(visitId))
//                .body("note", isA(String.class));

    }

    @Test
    public void testVisitDetailsEndpoint() {
        // Przygotowanie danych testowych, np. dodanie wizyty do bazy
        // lub wykorzystanie wersji zdefiniowanej w pliku data.sql

        int visitId = 1;

        given()
                .pathParam("visitId", visitId)
                .when()
                .get("/api/visits/{visitId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("visitId", equalTo(visitId))
                .body("note", notNullValue());
    }

    @Test
    void thatVisitDetailsShouldReturnNotFound() {

    }

    @Test
    void thatAddVisitWorksCorrectly() {
        VisitDTO visit = DtoFixtures.someVisit1();

        given()
                .contentType(ContentType.JSON)
                .body(visit)
                .when()
                .post(API_VISITS)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", startsWith(API_VISITS));
    }

    @Test
    void thatUpdateVisitWorksCorrectly() {

    }

    @Test
    void thatDeleteVisitWorksCorrectly() {
        int visitId = 1;

        given()
                .port(port)
                .pathParam("visitId", visitId)
                .when()
                .delete("/clinic/api/visits/{visitId}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void thatDeleteVisitShouldReturnNotFound() {
        int visitId = 1;

        given()
                .pathParam("visitId", visitId)
                .when()
                .delete(API_VISITS.concat(VISIT_ID))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void thatUpdateVisitNoteWorksCorrectly() {

    }


}
