package pl.taw.proby.jakoscpowietrza;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api-air")
@AllArgsConstructor
public class AirRestController {

    private final AirQualityService airQualityService;

    @GetMapping(value = "/q", produces = {MediaType.APPLICATION_JSON_VALUE})
    public String getAirQuality() {
        return airQualityService.getIndexLevelName();
    }

    @GetMapping(value = "/q-re", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> getAirQualityResponseEntity() {
        String indexLevelName = airQualityService.getIndexLevelName();
        return ResponseEntity.ok(indexLevelName);
    }
}
