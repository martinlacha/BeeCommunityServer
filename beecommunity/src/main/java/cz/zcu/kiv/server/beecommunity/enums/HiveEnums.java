package cz.zcu.kiv.server.beecommunity.enums;

public class HiveEnums {

    /**
     * Color of hive
     */
    public enum EColor {
        NONE,
        WHITE,
        BLACK,
        GRAY,
        BLUE,
        RED,
        PURPLE,
        YELLOW,
        ORANGE,
        GREEN
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
}
