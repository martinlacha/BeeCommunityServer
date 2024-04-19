package cz.zcu.kiv.server.beecommunity.jpa.dto.event;

import cz.zcu.kiv.server.beecommunity.enums.ApiaryEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    Long id;
    String title;
    ApiaryEnums.EEventActivityType activity;
    ApiaryEnums.EEventType type;
    String notes;
    String date;
    boolean isFinished;
}
