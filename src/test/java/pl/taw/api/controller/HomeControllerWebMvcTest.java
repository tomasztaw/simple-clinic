package pl.taw.api.controller;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.security.UserRepository;
import pl.taw.proby.jakoscpowietrza.AirQualityService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = HomeController.class) // tworzenie kontekstu tylko dla tego kontrolera
@AutoConfigureMockMvc(addFilters = false)
@AllArgsConstructor(onConstructor = @__(@Autowired))  // automatyczne dodanie Autowired nad konstruktorami
class HomeControllerWebMvcTest {

    @MockBean  // tworzenie zaślepionych beanów dla kontekstu spring-a, automatyczne wstrzykiwanie
    @SuppressWarnings("unused")
    private PatientDAO patientDAO;

    @MockBean
    @SuppressWarnings("unused")
    private UserRepository userRepository;

    // próby z api dla jakości powietrza
    @MockBean
    @SuppressWarnings("unused")
    private AirQualityService airQualityService;

    private MockMvc mockMvc;

    @Test
    void homeWorksCorrectly() throws Exception {
        // given, when, then
        mockMvc.perform(get(HomeController.HOME))
            .andExpect(status().isOk())
            .andExpect(view().name("home"));
    }

}