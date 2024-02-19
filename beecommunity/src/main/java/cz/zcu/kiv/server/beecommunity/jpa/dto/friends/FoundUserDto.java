package cz.zcu.kiv.server.beecommunity.jpa.dto.friends;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Dto with information about friend
 */

@Data
@AllArgsConstructor
public class FoundUserDto {
    private String email;
    private String name;
    private String surname;
}
