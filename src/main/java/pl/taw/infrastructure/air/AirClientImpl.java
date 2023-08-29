package pl.taw.infrastructure.air;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.taw.infrastructure.pjp.ApiClient;
import pl.taw.infrastructure.pjp.model.StationDataDTO;
import pl.taw.infrastructure.pjp.model.StationLd;

@Component
@AllArgsConstructor
public class AirClientImpl {

    private final Air air;
    private final StationDataDTO stationDataDTO;
    private final StationLd stationLd;
    private final ApiClient apiClient;



}
