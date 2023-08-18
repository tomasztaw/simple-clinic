package pl.taw.infrastructure.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import pl.taw.business.pogoda.WeatherService;

@Configuration
public class BeanConfiguration {

    // ###################################### Dodane dla konsumowania api place holder ##############################3
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Value("${openweathermap.apiKey}") // Wartość klucza API zostanie wstrzyknięta z pliku properties
    private String apiKey;
    @Bean
    public WeatherService weatherService(RestTemplate restTemplate) {
        return new WeatherService(apiKey, restTemplate);
    }

    // obsługiwanie PATCH w przeglądarce
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    // czy musi być static?
    @Bean
    public static ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new Jdk8Module())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
