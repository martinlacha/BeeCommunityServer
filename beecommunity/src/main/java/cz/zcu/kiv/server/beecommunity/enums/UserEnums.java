package cz.zcu.kiv.server.beecommunity.enums;

public class UserEnums {

    /**
     * Honorific of user
     */
    public enum EHonorific {
        MR,
        MRS,
        MS,
        MISS
    }

    /**
     * User experience level
     */
    public enum EExperience {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        PROFESSIONAL
    }

    /**
     * Types of role in application
     */
    public enum ERoles {
        USER,
        ADMIN;
    }
}
