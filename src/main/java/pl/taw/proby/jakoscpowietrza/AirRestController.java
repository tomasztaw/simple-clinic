package pl.taw.proby.jakoscpowietrza;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-air")
@AllArgsConstructor
public class AirRestController {

    private final AirQualityService airQualityService;

    @GetMapping(value = "/q", produces = {MediaType.APPLICATION_JSON_VALUE})
    public String getAirQuality() {
        return airQualityService.getIndexLevelName();
    }
}
