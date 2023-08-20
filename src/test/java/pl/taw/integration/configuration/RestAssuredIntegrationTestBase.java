package pl.taw.integration.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import pl.taw.integration.support.AuthenticationTestSupport;
import pl.taw.integration.support.ControllerTestSupport;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * w21-29
 */

public abstract class RestAssuredIntegrationTestBase
    extends AbstractIT
    implements ControllerTestSupport, AuthenticationTestSupport {

    // #####################
    @LocalServerPort
    private int serverPort;

    @Value("${server.servlet.context-path}")
    private String basePath;  // ścieżka bazowa (*/clinic)
    // ####################

    protected static WireMockServer wireMockServer;

    private String jSessionIdValue;

    @Autowired
    @SuppressWarnings("unused")
    protected ObjectMapper objectMapper;
//    private ObjectMapper objectMapper;

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Test
    void contextLoaded() {
        assertThat(true).isTrue();
    }

    @BeforeAll
    static void beforeAll() {
        wireMockServer = new WireMockServer(
            wireMockConfig()
                .port(9999)
                .extensions(new ResponseTemplateTransformer(false))
        );
        wireMockServer.start();
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

    @BeforeEach
    void beforeEach() {
        jSessionIdValue = login("tomek", "test")
            .and()
            .cookie("JSESSIONID")
            .header(HttpHeaders.LOCATION, "http://localhost:%s%s/".formatted(port, basePath))
            .extract()
            .cookie("JSESSIONID");
    }

    @AfterEach
    void afterEach() {
        logout()
            .and()
            .cookie("JSESSIONID", "");
        jSessionIdValue = null;
        wireMockServer.resetAll();
    }

    public RequestSpecification requestSpecification() {
        return restAssuredBase()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .cookie("JSESSIONID", jSessionIdValue);
    }

    public RequestSpecification requestSpecificationNoAuthentication() {
        return restAssuredBase();
    }

    private RequestSpecification restAssuredBase() {
        return RestAssured
            .given()
            .config(getConfig())
            .basePath(basePath)
            .port(port);
    }

    private RestAssuredConfig getConfig() {
        return RestAssuredConfig.config()
            .objectMapperConfig(new ObjectMapperConfig()
                .jackson2ObjectMapperFactory((type, s) -> objectMapper));
    }

// ************************  Później metoda rozbita na dwie
//    public RequestSpecification requestSpecification() {
//        return RestAssured
//                .given()
//                .config(getConfig())
//                .basePath(basePath)
//                .port(port)
//                .accept(ContentType.JSON)
//                .contentType(ContentType.JSON);
//    }
}
