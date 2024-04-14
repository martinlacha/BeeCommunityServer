package cz.zcu.kiv.server.beecommunity.jpa.dto.hive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SensorDataDto {

    private String time;

    private double weight;

    private double hiveTemperature;

    private double hiveHumidity;

    private double outsideTemperature;

    private double outsideHumidity;
}
