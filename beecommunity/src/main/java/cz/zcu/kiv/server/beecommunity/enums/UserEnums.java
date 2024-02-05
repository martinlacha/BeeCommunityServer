package cz.zcu.kiv.server.beecommunity.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class UserEnums {
    public enum ETitle {
        MR,
        MRS,
        MS,
        MISS
    }
    public enum EExperience {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        PROFESSIONAL
    }

    public enum ERoles {
        USER,
        ADMIN;
    }
}
