package pl.taw.proby.jakoscpowietrza;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/air")
public class AirController {

    private final AirQualityService airQualityService;

    @GetMapping("/airquality")
    public String getAirQuality(Model model) {
        String indexLevelName = airQualityService.getIndexLevelName();
        String stCalcDate = airQualityService.getStCalcDate();
        List<SensorData> sensorDataList = airQualityService.getSensorData();

        String backgroundColor = getBackgroundColor(indexLevelName);

        model.addAttribute("indexLevelName", indexLevelName);
        model.addAttribute("stCalcDate", stCalcDate);
        model.addAttribute("sensorDataList", sensorDataList);

        model.addAttribute("backgroundColor", backgroundColor);

        return "air";
    }

    private String getBackgroundColor(String indexLevelName) {
        switch (indexLevelName) {
            case "Bardzo dobry" -> { return "rgba(0, 255, 0, 0.3)"; }
            case "Dobry" -> { return "rgba(0, 120, 0, 0.3)"; }
            case "Umiarkowany" -> { return "rgba(255, 255, 0, 0.3)"; }
            case "Zły" -> { return "rgba(255, 153, 0, 0.3)"; }
            case "Bardzo zły" -> { return "rgba(255, 0, 0, 0.3)"; }
            default -> { return ""; }
        }
    }
}
