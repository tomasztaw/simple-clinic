package pl.taw.integration.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import pl.taw.integration.configuration.AbstractIT;
import pl.taw.integration.configuration.TestSecurityConfig;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.taw.api.controller.rest.ReservationRestController.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@Sql("/reservationsDataForTest.sql")
public class ReservationRestAssuredControllerIT extends AbstractIT {

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
    void thatGetReservationAsListWorksCorrectly() {
        given()
                .when()
                .get(API_RESERVATIONS)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0))
                .body("reservations", hasSize(6))
                .body("reservations.reservationId", everyItem(isA(Integer.class)))
                .body("reservations.doctorId", everyItem(isA(Integer.class)))
                .body("reservations.patientId", everyItem(isA(Integer.class)))
                .body("reservations.day", everyItem(notNullValue()))
                .body("reservations.day", everyItem(matchesRegex("\\d{4}-\\d{2}-\\d{2}")))
                .body("reservations.startTimeR", everyItem(notNullValue()))
//                .body("reservations.startTimeR", everyItem(matchesRegex("\\d{2}:\\d{2}")))
                .body("reservations.startTimeR", everyItem(matchesRegex("\\d{2}:\\d{2}:\\d{2}")))
                .body("reservations.occupied", everyItem(notNullValue()));
    }

    @Test
    void thatGetReservationDetailsWorksCorrectly() {
        Integer reservationId = 1;

        given()
                .when()
                .get(API_RESERVATIONS.concat(RESERVATION_ID), reservationId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("reservationId", equalTo(reservationId));
//                .body("doctorId", isA(Integer.class));
    }

    @Test
    void thatGetReservationDetailsWorksCorrectly3() {
        Integer reservationId = 1;

        given()
                .pathParam("reservationId", reservationId)
                .accept(ContentType.JSON)
                .when()
                .get(API_RESERVATIONS.concat(RESERVATION_ID), reservationId)
                .then()
                .statusCode(HttpStatus.OK.value());
//                .contentType(ContentType.JSON)
//                .body("$", hasKey("reservationId"))
//                .body("reservationId", equalTo(reservationId))
//                .body("doctorId", isA(Integer.class));
    }

    @Test
    void thatGetReservationDetailsWorksCorrectly2() {
        Integer reservationId = 1;
//        Integer reservationId = 6;


        given()
                .pathParam("reservationId", reservationId)
                .when().log().all()
                .get(API_RESERVATIONS.concat(RESERVATION_ID), reservationId)
                .then()
                .statusCode(200);
//                .body("reservationId", equalTo(reservationId))
//                .body("doctorId", isA(Integer.class));
    }

    @Test
    void should() {
        given().when().get(API_RESERVATIONS.concat("/get"))
                .then()
                .statusCode(200);
    }


    @Test
    void thatGetReservationDetailsShouldReturnNoFound() {
        int reservationId = 88;

//        given()
//                .pathParam("reservationId", reservationId)
//                .when()
//                .get(API_RESERVATIONS.concat(RESERVATION_ID))
//                .then()
//                .statusCode(404);

        Response response = given()
                .pathParam("reservationId", reservationId)
                .when()
                .get(API_RESERVATIONS.concat(RESERVATION_ID))
                .then()
                .extract().response();

        // Sprawdzanie statusu
//        assertEquals(404, response.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    @Test
    void thatGetAllReservationByDateWorksCorrectly() {
        LocalDate day = LocalDate.of(2023, 8, 4);

        given()
                .pathParam("day", day.toString())
                .when()
                .get(API_RESERVATIONS.concat(BY_DATE))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", equalTo(2))
                .body("reservations.day", everyItem(equalTo(day)));
    }

    @Test
    void thatGetAllReservationByDateWorksCorrectly2() {
        int year = 2023;
        int month = 8;
        int dayOfMonth = 4;

        given()
                .pathParam("day", String.format("%d-%02d-%02d", year, month, dayOfMonth))
                .when()
                .get(API_RESERVATIONS.concat(BY_DATE))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", equalTo(2));
//                .body("reservations.day", everyItem(equalTo(day)));
    }

    @Test
    void thatGetAllReservationByDateShouldReturnNoContent() {

    }

    @Test
    void thatAddReservationWorksCorrectly() {

    }

    @Test
    void thatUpdateReservationWorksCorrectly() {

    }

    @Test
    void thatDeleteReservationWorksCorrectlyAndReturnNoContent() {

    }

    @Test
    void thatDeleteReservationShouldReturnNoContent() {

    }

    @Test
    void thatUpdateReservationDateWorksCorrectly() {

    }
}
