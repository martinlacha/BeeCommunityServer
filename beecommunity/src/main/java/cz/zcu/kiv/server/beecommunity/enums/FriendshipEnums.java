package cz.zcu.kiv.server.beecommunity.enums;

public class FriendshipEnums {

    /**
     * Status of friendship between users
     */
    public enum EStatus {

        /**
         * Friend users. Users after accept PENDING or SEND_TO_ME request
         */
        FRIEND,

        /**
         * Blocked users. Blocking users can not send you request for friendship
         */
        BLOCKED,

        /**
         * Requests send to another users
         */
        PENDING,

        /**
         * None relation between users
         */
        NONE
    }

}
