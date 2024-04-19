package cz.zcu.kiv.server.beecommunity.enums;

public class CommunityEnums {

    /**
     * Community post access types
     */
    public enum EAccess {

        /**
         * Public access to community post that everyone can read and react
         */
        PUBLIC,

        /**
         * Private access to community post only for friends can read and react
         */
        PRIVATE,
    }

    /**
     * Community post type
     */
    public enum EType {
        /**
         * None type
         */
        NONE,
        /**
         * Information post
         */
        INFO,

        /**
         * Warning post
         */
        WARN,

        /**
         * Emergency post
         */
        EMERGENCY
    }
}
