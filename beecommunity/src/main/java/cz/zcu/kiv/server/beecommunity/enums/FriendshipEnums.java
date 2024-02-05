package cz.zcu.kiv.server.beecommunity.enums;

public class FriendshipEnums {
    public enum EStatus {
        // Other users. Users that can be search and send request for friendship
        OTHER,

        // Friend users. Users after accept PENDING or SEND_TO_ME request
        FRIEND,

        // Blocked users. Blocking users can not send you request for friendship
        BLOCKED,

        // Requests i send to another users
        PENDING,

        // Requests send to me from another users
        SEND_TO_ME
    }

}
