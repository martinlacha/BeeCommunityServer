package cz.zcu.kiv.server.beecommunity.jpa.dto.event;

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
    String activity;
    String type;
    String notes;
    String date;
    boolean isFinished;
}
