package cz.zcu.kiv.server.beecommunity.enums;

public class InspectionEnums {

    /**
     * Weather type when inspection
     */
    public enum EWeather {
        CLEAR,
        PARTLY_CLOUDY,
        CLOUDY,
        DRIZZLE,
        RAIN,
        STORM,
        SNOW,
        FOG
    }

    /**
     * Bee population in hive
     */
    public enum EPopulation {
        VERY_GOOD,
        NORMAL,
        LOW,
        VERY_LOW
    }

    /**
     * Amount of food storage in hive
     */
    public enum EFoodStorage {
        VERY_GOOD,
        NORMAL,
        LOW,
        VERY_LOW
    }

    /**
     * Sources nearby hive
     */
    public enum ESourceNearby {
        HIGH,
        MEDIUM,
        LOW,
        NONE
    }

    /**
     * Brood pattern in hive
     */
    public enum EBroodPattern {
        NO_BROOD,
        VERY_SPOTTY,
        SPOTTY,
        MOSTLY_SOLID,
        SOLID,
        OTHER
    }

    /**
     * Bees behaviour in colony
     */
    public enum EColonyTemperament {
        CALM,
        NERVOUS,
        AGGRESSIVE,
        OTHER
    }
}
