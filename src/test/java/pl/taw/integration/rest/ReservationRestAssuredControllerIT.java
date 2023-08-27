package pl.taw.integration.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.infrastructure.database.entity.ReservationEntity;
import pl.taw.integration.configuration.AbstractIT;
import pl.taw.integration.configuration.TestSecurityConfig;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.taw.api.controller.rest.DoctorRestController.API_DOCTORS;
import static pl.taw.api.controller.rest.ReservationRestController.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@Sql("/reservationsDataForTest.sql")
//@Transactional
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
        Integer reservationId = 101;

        given()
                .pathParam("reservationId", reservationId)
                .accept(ContentType.JSON)
                .when()
                .get(API_RESERVATIONS.concat(RESERVATION_ID), reservationId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("reservationId", equalTo(reservationId))
                .body("day", is(matchesRegex("\\d{4}-\\d{2}-\\d{2}")))
                .body("startTimeR", is(matchesRegex("\\d{2}:\\d{2}:\\d{2}")))
                .body("doctorId", isA(Integer.class));
    }

    @Test
    void thatGetReservationDetailsShouldReturnNoFound() {
        int reservationId = -88;

        given()
                .pathParam("reservationId", reservationId)
                .accept(ContentType.JSON)
                .when()
                .get(API_RESERVATIONS.concat(RESERVATION_ID))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void thatGetAllReservationByDateWorksCorrectly() {
        LocalDate day = LocalDate.of(2023, 8, 24);

        given()
                .pathParam("day", day.toString())
                .when()
                .get(API_RESERVATIONS.concat(BY_DATE))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0))
                .body("reservations", hasSize(2))
                .body("reservations.day", everyItem(equalTo(day.toString())));
    }

    @Test
    void thatGetAllReservationByDateShouldReturnNoContent() {
        LocalDate day = LocalDate.of(2023, 8, 6);

        given()
                .pathParam("day", day.toString())
                .when()
                .get(API_RESERVATIONS.concat(BY_DATE))
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void thatAddReservationWorksCorrectly() {
        ReservationDTO reservation = ReservationDTO.builder()
                .doctorId(2)
                .patientId(3)
                .day(LocalDate.of(2023, 8, 28))
                .startTimeR(LocalTime.of(10, 0))
                .occupied(true)
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(reservation)
                .when()
                .post(API_RESERVATIONS)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", matchesPattern(API_RESERVATIONS + "/\\d+"));
    }

    @Test
    void thatUpdateReservationWorksCorrectly() {
        Integer reservationId = 101;
        ReservationDTO reservation = ReservationDTO.builder()
                .doctorId(4)
                .patientId(4)
                .day(LocalDate.of(2023, 8, 29))
                .startTimeR(LocalTime.of(10, 0))
                .occupied(true)
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservation)
                .when()
                .put(API_RESERVATIONS.concat(RESERVATION_ID), reservationId)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testUpdateReservation() {
        Integer reservationId = 101;
        ReservationEntity existingReservation = given()
                .pathParam("reservationId", reservationId)
                .accept(ContentType.JSON)
                .when()
                .get(API_RESERVATIONS.concat(RESERVATION_ID), reservationId).as(ReservationEntity.class);

        ReservationDTO updatedReservationDTO = ReservationDTO.builder()
                .doctorId(5)
                .patientId(5)
                .day(LocalDate.of(2023, 8, 28))
                .startTimeR(LocalTime.of(10, 0))
                .occupied(true)
                .build();

        given()
                .contentType("application/json")
                .body(updatedReservationDTO)
                .when()
                .put(API_RESERVATIONS.concat(RESERVATION_ID), existingReservation.getReservationId())
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void thatDeleteReservationWorksCorrectlyAndReturnNoContent() {
        Integer reservationId = 105;

        given()
                .when()
                .delete(API_RESERVATIONS.concat(RESERVATION_ID), reservationId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void thatDeleteReservationShouldReturnNoContent() {
        Integer reservationId = -5;

        given()
                .when()
                .delete(API_RESERVATIONS.concat(RESERVATION_ID), reservationId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void thatUpdateReservationDateWorksCorrectly() {
        ReservationEntity existingReservation = EntityFixtures.someReservation1().withReservationId(102);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime newDateTime = LocalDateTime.of(2023, 8, 30, 15, 30);

        String formattedDateTime = newDateTime.format(formatter);

        given()
                .param("dateTime", formattedDateTime)
                .when()
                .patch(API_RESERVATIONS.concat(RESERVATION_UPDATE_DATE), existingReservation.getReservationId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(emptyOrNullString());
    }
}
