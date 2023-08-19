package pl.taw.proby.jakoscpowietrza;

import lombok.*;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirQualityResponse {

    private long id;
    private String stCalcDate;
    private IndexLevel stIndexLevel;
    private String stSourceDataDate;
    private String so2CalcDate;
    private IndexLevel so2IndexLevel;
    private String so2SourceDataDate;
    private String no2CalcDate;
    private IndexLevel no2IndexLevel;



}
