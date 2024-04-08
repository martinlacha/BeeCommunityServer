package cz.zcu.kiv.server.beecommunity.jpa.dto.friends;

import cz.zcu.kiv.server.beecommunity.enums.FriendshipEnums;
import cz.zcu.kiv.server.beecommunity.enums.UserEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto with information about friend
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoundUserDto {
    private String email;
    private String name;
    private String surname;
    private String state;
    private String country;
    private String town;
    private UserEnums.EExperience experience;
}
