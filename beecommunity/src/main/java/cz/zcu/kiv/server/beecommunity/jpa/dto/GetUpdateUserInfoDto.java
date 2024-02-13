package cz.zcu.kiv.server.beecommunity.jpa.dto;

import cz.zcu.kiv.server.beecommunity.enums.UserEnums;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

/**
 * Dto with address and pesonal user information
 */
@Data
public class GetUpdateUserInfoDto {
    private String name;
    private String surname;
    @Past(message = "Date of birth has to be from past")
    private LocalDate dateOfBirth;
    private UserEnums.EExperience experience;
    private String state;
    private String country;
    private String town;
    private String street;
    private int number;
}
