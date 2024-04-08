package cz.zcu.kiv.server.beecommunity.jpa.dto.friends;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto with information about friend
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoundUserDto {
    private String email;
    private String name;
    private String surname;
    private String state;
    private String country;
    private String town;
}
