package cz.zcu.kiv.server.beecommunity.jpa.dto;

import cz.zcu.kiv.server.beecommunity.enums.UserEnums;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserInfoDto {
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private UserEnums.EExperience experience;
    private String state;
    private String country;
    private String town;
    private String street;
    private int number;
}
