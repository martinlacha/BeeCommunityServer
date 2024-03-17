package cz.zcu.kiv.server.beecommunity.enums;

public class HiveEnums {

    /**
     * Color of hive
     */
    public enum EColor {
        NONE,
        BLACK,
        PURPLE,
        BLUE,
        GREEN,
        YELLOW,
        ORANGE,
        RED,
        WHITE,
        GRAY,
        OTHER
    }

    /**
     * Source where bees comes from
     */
    public enum EBeeSource {
        SWARM,
        NUC,
        PACKAGE,
        SPLIT,
        ACQUIRED,
        OTHER
    }

    /**
     * Parts of beehive
     */
    public enum EBodyPart {
        OUTER_COVER,
        MEDIUM_SUPER,
        DEEP_SUPER,
        SLATTED_RACK,
        QUEEN_EXCLUDER,
        BOTTOM_BOARD,
        HIVE_STAND
    }
}
