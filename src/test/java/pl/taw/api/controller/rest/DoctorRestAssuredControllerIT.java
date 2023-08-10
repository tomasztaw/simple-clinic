package pl.taw.api.controller.rest;

import static org.junit.jupiter.api.Assertions.*;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.boot.web.server.LocalServerPort;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class DoctorRestAssuredControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost:%s/clinic".formatted(port);
//        RestAssured.baseURI = "http://localhost";
//        RestAssured.baseURI = "http://localhost:8080/clinic";
        RestAssured.port = port;
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"}) // Dodaj role, które są wymagane do dostępu
    public void testShowDoctorByIdXXX() throws Exception {
        int doctorId = 1;

        mockMvc.perform(get("/api/doctors/{doctorId}", doctorId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("doctorId").value(doctorId));
    }

    @Test
    public void testShowDoctorById() {
        int doctorId = 1; // Zmień na odpowiedni numer pacjenta do przetestowania

        given()
                .pathParam("doctorId", doctorId)
                .when()
                .get("/api/doctors/{doctorId}")
                .then()
                .statusCode(200) // Oczekiwany kod statusu
                .contentType("application/json") // Oczekiwany typ zawartości
                .body("id", equalTo(doctorId)); // Oczekiwane pole 'id'
    }

}