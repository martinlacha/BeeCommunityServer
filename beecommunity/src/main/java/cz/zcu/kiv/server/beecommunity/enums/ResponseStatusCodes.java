package cz.zcu.kiv.server.beecommunity.enums;

import lombok.Getter;

/**
 * Class for special return status codes
 */
@Getter
public enum ResponseStatusCodes {

    /**
     * Status code when account try to send authorised request but account is blocked
     * If account is blocked is checked in JWT filter
     */
    ACCOUNT_LOCKED_STATUS_CODE(420);

    /**
     * Integer represent of status code
     */
    private final int code;

    ResponseStatusCodes(final int code) {
        this.code=code;
    }
}
