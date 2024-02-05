package cz.zcu.kiv.server.beecommunity.exceptions;

public class UserNotFoundByEmailException extends Exception {
    public UserNotFoundByEmailException() {
        super("User not found by email");
    }
}
