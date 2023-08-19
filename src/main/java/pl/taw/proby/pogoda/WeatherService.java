package pl.taw.proby.pogoda;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

//    private final String apiKey;
//    private final RestTemplate restTemplate;
//
//    public WeatherService(String apiKey, RestTemplate restTemplate) {
//        this.apiKey = apiKey;
//        this.restTemplate = restTemplate;
//    }
//
//    public WeatherData getWeatherData(String city) {
//        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
//        return restTemplate.getForObject(url, WeatherData.class);
//    }
}
