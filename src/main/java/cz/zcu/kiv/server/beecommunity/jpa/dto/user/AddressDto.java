package cz.zcu.kiv.server.beecommunity.jpa.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto with information about user address
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private String state;
    private String country;
    private String town;
    private String zip;
    private String street;
    private int number;
}
