package cz.zcu.kiv.server.beecommunity.enums;

public class ApiaryEnums {
    /**
     * Enum for describe surrounding environment of apiary
     */
    public enum EEnvironment {
        RURAL,
        SUBURBAN,
        URBAN
    }

    /**
     * Enum for describe terrain where is apiary
     */
    public enum ETerrain {
        BACKYARD,
        GRASSLAND,
        FOREST,
        MOUNTAINS,
        COASTAL,
        DESERT,
        ROOFTOP,
        OTHER
    }

    /**
     * Enum to describe event type
     */
    public enum EEventType {
        GENERAL,
        APIARY,
        HIVE
    }

    /**
     * Enum to describe event activity type in calendar
     */
    public enum EEventActivityType {
        FEED,
        HEAL,
        INSPECT,
        DISEASE,
        QUEEN,
        SWARN,
        MEET,
        OTHER
    }

}
