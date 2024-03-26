package cz.zcu.kiv.server.beecommunity.enums;

public class InspectionEnums {

    /**
     * Type of inspection
     */
    public enum EType {
        INSPECTION,
        HARVEST,
        FEEDING,
        TREATMENTS,
        STRESSORS
    }

    /**
     * Weather type when inspection
     */
    public enum EWeather {
        THUNDERSTORM,
        DRIZZLE,
        RAIN,
        SNOW,
        ATMOSPHERE,
        CLEAR,
        CLOUDS
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
        UNSPECIFIED,
        CALM,
        NERVOUS,
        AGGRESSIVE,
        OTHER
    }

    /**
     * Image type
     */
    public enum EImageType {
        INSPECTION,
        POPULATION,
        FOOD,
        QUEEN,
        BROOD,
        STRESSORS,
        DISEASE
    }

    /**
     * Treatment dose type
     */
    public enum EUnitsAndDoses {
        UNSPECIFIED,
        STRIP,
        DROP,
        KILOGRAM,
        GRAM,
        LITER,
        MILLILITER,
        PIECE
    }

    /**
     * Harvest product type
     */
    public enum EHarvestProduct {
        UNSPECIFIED,
        HONEY,
        WAX,
        PROPOLIS,
        POLLEN,
        VENOM,
        ROYAL_JELLY
    }

    /**
     * Food ratio
     */
    public enum EFoodRatio {
        NONE,
        ONE_ZERO,
        ONE_ONE,
        TWO_ONE,
        THREE_ONE,
        ONE_TWO,
        ONE_THREE
    }

    /**
     * Food type
     */
    public enum EFoodType {
        UNSPECIFIED,
        SUGAR,
        HONEY,
        SYRUP,
        POLLEN,
        NECTAR
    }

    /**
     * Disease type
     */
    public enum EDisease {
        UNSPECIFIED,
        VARROASIS,
        NOSEMA,
        CHLKBROOD,
        SACBROOD,
        FOULBROOD
    }
}
