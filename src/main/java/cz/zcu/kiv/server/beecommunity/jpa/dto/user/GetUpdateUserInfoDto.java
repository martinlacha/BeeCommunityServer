package cz.zcu.kiv.server.beecommunity.jpa.dto.user;

import cz.zcu.kiv.server.beecommunity.enums.UserEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto with address and personal user information
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUpdateUserInfoDto {
    private String name;
    private String surname;
    private String dateOfBirth;
    private UserEnums.EExperience experience;
    private String state;
    private String country;
    private String town;
    private String street;
    private int number;
    private boolean isAdmin;
}
