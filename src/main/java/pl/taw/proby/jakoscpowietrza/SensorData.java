package pl.taw.proby.jakoscpowietrza;

import lombok.*;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorData {

    private Long id;
    private Long stationId;
    private Param param;

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Param {

        private String paramName;
        private String paramFormula;
        private String paramCode;
        private Long idParam;

    }
}
