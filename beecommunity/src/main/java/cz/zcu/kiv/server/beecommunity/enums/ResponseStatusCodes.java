package cz.zcu.kiv.server.beecommunity.enums;

public enum ResponseStatusCodes {
    ACCOUNT_LOCKED_STATUS_CODE(420);

    private final int code;

    ResponseStatusCodes(final int code) {
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
