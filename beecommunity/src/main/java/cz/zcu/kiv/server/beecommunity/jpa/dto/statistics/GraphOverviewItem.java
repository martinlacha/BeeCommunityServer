package cz.zcu.kiv.server.beecommunity.jpa.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GraphOverviewItem {
    String date;
    Long count;
}
