package pl.taw.integration.rest;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.hibernate.query.sqm.mutation.internal.temptable.LocalTemporaryTableInsertStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import pl.taw.api.controller.rest.ReservationRestController;
import pl.taw.integration.configuration.AbstractIT;
import pl.taw.integration.configuration.TestSecurityConfig;

import java.time.LocalDate;
import java.time.LocalTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.taw.api.controller.rest.OpinionRestController.*;
import static pl.taw.api.controller.rest.ReservationRestController.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
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
                .body("reservations.reservationId", everyItem(isA(Integer.class)))
                .body("reservations.doctorId", everyItem(isA(Integer.class)))
                .body("reservations.patientId", everyItem(isA(Integer.class)))
                .body("reservations.day", everyItem(isA(LocalDate.class)))
                .body("reservations.startTimeR", everyItem(isA(LocalTime.class)))
                .body("reservations.occupied", everyItem(notNullValue()));
    }

    @Test
    void thatGetReservationDetailsWorksCorrectly() {
        int reservationId = 1;

        given()
                .when()
                .get(API_RESERVATIONS.concat(RESERVATION_ID), reservationId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("reservationId", equalTo(reservationId))
                .body("doctorId", isA(Integer.class));
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
