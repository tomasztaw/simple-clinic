package pl.taw.integration.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.integration.configuration.TestSecurityConfig;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.taw.api.controller.rest.DoctorRestController.*;

//import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class DoctorRestAssuredControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost:%s/clinic".formatted(port);
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
    }

//    @MockBean
//    private DoctorRepository doctorRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"}) // niepotrzebne dla wyłączonego security
    public void testShowDoctorByIdWithUser() throws Exception {
        int doctorId = 1;

        mockMvc.perform(get(API_DOCTORS.concat(DOCTOR_ID), doctorId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("doctorId").value(doctorId));
    }

    @Test
    public void testShowDoctorById() {
        int doctorId = 2;

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
                .body("doctors[0].title", equalTo("Laryngolog"));
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
                .body("size()", greaterThan(0));
//                .body("visits[0].doctor.doctorId", equalTo(doctorId));
    }

    @Test
    public void testAddDoctor() {
        String requestBody = "{\"name\":\"Adam\",\"surname\":\"Nowiczok\",\"title\":\"Dermatolog\",\"phone\":\"+48 120 456 333\",\"email\":\"a.nowiczok@eclinic\"}";

        DoctorEntity doctorEntity = DoctorEntity.builder()
                .name("Adam")
                .surname("Nowiczok")
                .title("Dermatolog")
                .phone("+48 120 456 333")
                .email("a.nowiczok@eclinic.pl")
                .build();

//        Mockito.when(doctorRepository.saveAndReturn(ArgumentMatchers.any(DoctorEntity.class))).thenReturn(doctorEntity);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(API_DOCTORS)
                .then()
                .statusCode(HttpStatus.CREATED.value());

//        Mockito.verify(doctorRepository, Mockito.times(1)).saveAndReturn(Mockito.any(DoctorEntity.class));
    }

    @Test
    @Disabled("problem z parsowaniem")
    public void testUpdateDoctor() {
        String requestBody = "{\"name\":\"Jan\",\"surname\":\"Kowalczyk\",\"title\":\"Toksykolog\",\"phone\":\"+48 120 456 455\",\"email\":\"jan.kowalczyk@eclinic.pl\"}";

        int doctorId = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(API_DOCTORS)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().path("doctorId");

        String updatedRequestBody = "{\"name\":\"Janusz\",\"surname\":\"Kowalczyk\",\"title\":\"Toksykolog\",\"phone\":\"+48 120 456 456\",\"email\":\"janusz.kowalczyk@eclinic.pl\"}";

        given()
                .contentType(ContentType.JSON)
                .body(updatedRequestBody)
                .when()
                .put(API_DOCTORS.concat(DOCTOR_ID), doctorId)
                .then()
                .statusCode(HttpStatus.OK.value());
    }


}