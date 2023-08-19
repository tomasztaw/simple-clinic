package pl.taw.proby.jakoscpowietrza;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class AirQualityService {

    public static final String  API_URL = "https://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/117";
    public static final String  API_URL_SENSOR = "https://api.gios.gov.pl/pjp-api/rest/station/sensors/117";

    private final RestTemplate restTemplate;

    public String getIndexLevelName() {
        AirQualityResponse response = restTemplate.getForObject(API_URL, AirQualityResponse.class);

        if (response != null && response.getStIndexLevel() != null) {
            return response.getStIndexLevel().getIndexLevelName();
        } else {
            return "Brak danych";
        }
    }

    public String getStCalcDate() {
        AirQualityResponse response = restTemplate.getForObject(API_URL, AirQualityResponse.class);

        if (response != null) {
            return response.getStCalcDate();
        } else {
            return "Brak danych";
        }
    }

    public List<SensorData> getSensorData() {
        ResponseEntity<SensorData[]> response = restTemplate.exchange(API_URL_SENSOR, HttpMethod.GET, null, SensorData[].class);
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    public List<String> getPMParams() {
        List<String> pmParams = new ArrayList<>();
        SensorData response = restTemplate.getForObject(API_URL_SENSOR, SensorData.class);
        return pmParams;
    }
}
