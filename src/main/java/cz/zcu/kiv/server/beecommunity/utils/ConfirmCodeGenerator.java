package cz.zcu.kiv.server.beecommunity.utils;

import java.util.Random;

/**
 * Class for generating confirm code to restart password
 */

public class ConfirmCodeGenerator {
    private ConfirmCodeGenerator() {

    }

    static final int FIRST_CHAR = 48; // numeral '0' ASCII
    static final int LAST_CHAR = 122; // letter 'z' ASCII
    static final int CODE_LENGTH = 6;
    static Random random = new Random();

    /**
     * Generate new confirmation code
     * @return String value of confirmation code
     */
    public static String generateCode() {
        return random
                .ints(FIRST_CHAR, LAST_CHAR + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(CODE_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

    }
}
