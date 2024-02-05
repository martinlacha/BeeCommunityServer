package cz.zcu.kiv.server.beecommunity.enums;

public class InspectionEnums {
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

    public enum EPopulation {
        VERY_GOOD,
        NORMAL,
        LOW,
        VERY_LOW
    }

    public enum EFoodStorage {
        VERY_GOOD,
        NORMAL,
        LOW,
        VERY_LOW
    }

    public enum ESourceNearby {
        HIGH,
        MEDIUM,
        LOW,
        NONE
    }

    public enum EBroodPattern {
        NO_BROOD,
        VERY_SPOTTY,
        SPOTTY,
        MOSTLY_SOLID,
        SOLID,
        OTHER
    }

    public enum EColonyTemperament {
        CALM,
        NERVOUS,
        AGGRESSIVE,
        OTHER
    }
}
