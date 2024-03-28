package cz.zcu.kiv.server.beecommunity.jpa.dto.statistics;

import cz.zcu.kiv.server.beecommunity.jpa.dto.friends.FoundUserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dto with list of friends and statistics from friends
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendsStatisticsDto {
    private List<FoundUserDto> friends;
}
