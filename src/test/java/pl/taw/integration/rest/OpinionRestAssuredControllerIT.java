package pl.taw.integration.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.infrastructure.database.entity.OpinionEntity;
import pl.taw.integration.configuration.AbstractIT;
import pl.taw.integration.configuration.TestSecurityConfig;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static pl.taw.api.controller.rest.OpinionRestController.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class OpinionRestAssuredControllerIT extends AbstractIT {

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
    void testGetOpinionsWorksCorrectly() {
        given()
                .when()
                .get(API_OPINIONS)
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("opinions.opinionId", everyItem(notNullValue()))
                .body("opinions.doctorId", everyItem(notNullValue()))
                .body("opinions.patientId", everyItem(notNullValue()))
                .body("opinions.visitId", everyItem(notNullValue()))
                .body("opinions.comment", everyItem(notNullValue()));

    }

    @Test
    void thatGetOpinionDetailsWorksCorrectly() {
        int opinionId = 2;

        given()
                .pathParam("opinionId", opinionId)
                .when()
                .get(API_OPINIONS.concat(OPINION_ID))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("opinionId", equalTo(opinionId))
                .body("doctorId", notNullValue())
                .body("patientId", notNullValue())
                .body("visitId", notNullValue())
                .body("comment", notNullValue());
    }

    @Test
    void thatGetOpinionDetailsShouldReturnNotFoundWhenInvalidId() {
        int invalidId = -2;

        given()
                .pathParam("opinionId", invalidId)
                .when()
                .get(API_OPINIONS.concat(OPINION_ID))
                .then()
                .statusCode(404);
    }

    @Test
    void thatGetAllOpinionCommentWorksCorrectly() {
        given()
                .when()
                .get(API_OPINIONS.concat(COMMENTS))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0))
                .body("$", hasSize(greaterThan(0)));
    }

    @Test
    void thatCreateOpinionShouldWorksCorrectly() {
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("doctorId", "3");
        jsonMap.put("patientId", "4");
        jsonMap.put("visitId", "8");
        jsonMap.put("comment", "Jestem bardzo zadowolony");
        jsonMap.put("createdAt", "2023-08-15T12:30:00");

        given()
                .contentType(ContentType.JSON)
                .body(jsonMap)
                .when()
                .post(API_OPINIONS)
                .then()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.CREATED.value())
                .body("comment", equalTo("Jestem bardzo zadowolony"));
    }

    @Test
    void thatUpdateOpinionShouldWorkCorrectly() {
        int opinionId = 1;
        OpinionDTO opinion = given().when().get(API_OPINIONS.concat(OPINION_ID), opinionId).as(OpinionDTO.class);

        Map<String, String> updateJsonMap = new HashMap<>();
        updateJsonMap.put("opinionId", "1");
        updateJsonMap.put("doctorId", "5");
        updateJsonMap.put("patientId", "6");
        updateJsonMap.put("visitId", "9");
        updateJsonMap.put("comment", "Nowy komentarz");
        updateJsonMap.put("createdAt", "2023-08-20T14:00:00");

        OpinionEntity opinionEntity = OpinionEntity.builder()
                .opinionId(opinion.getOpinionId())
                .doctorId(opinion.getDoctorId())
                .patientId(opinion.getPatientId())
                .visitId(opinion.getVisitId())
                .comment(updateJsonMap.get("comment"))
                .createdAt(opinion.getCreatedAt())
                .build();

        given()
                .contentType(ContentType.JSON)
                .pathParam("opinionId", opinionId)
                .body(opinionEntity)
//                .body(updateJsonMap)
                .when()
                .put(API_OPINIONS.concat(UPDATE_BY_ID))
                .then()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value())
//                .body("comment", equalTo("Nowy pozytywny komentarz"));
                .body("comment", equalTo("Nowy komentarz"));
    }

    @Test
    void thatDeleteOpinionShouldReturnNoContent() {
        int opinionId = 3;

        given()
                .pathParam("opinionId", opinionId)
                .when()
                .delete(API_OPINIONS.concat(OPINION_ID))
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void thatDeleteOpinionShouldReturnNoFound() {
        int opinionId = -3;

        given()
                .pathParam("opinionId", opinionId)
                .when()
                .delete(API_OPINIONS.concat(OPINION_ID))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void thatUpdateOpinionCommentShouldWorkCorrectly() {
        int opinionId = 2;
        String updatedComment = "Dobrze, dobrze, dobrze";

        given()
                .queryParam("updatedComment", updatedComment)
                .when()
                .patch(API_OPINIONS.concat(OPINION_UPDATE_NOTE), opinionId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(containsString(updatedComment));
    }

}