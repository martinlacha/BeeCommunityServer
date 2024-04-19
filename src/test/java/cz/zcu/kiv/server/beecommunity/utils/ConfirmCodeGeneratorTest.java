package cz.zcu.kiv.server.beecommunity.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfirmCodeGeneratorTest {
    @Test
    void testGenerateCode() {
        // Generating the code
        String code = ConfirmCodeGenerator.generateCode();
        // Assertions
        assertNotNull(code);
        assertEquals(ConfirmCodeGenerator.CODE_LENGTH, code.length());

        for (int i = 0; i < code.length(); i++) {
            int character = code.charAt(i);
            assertTrue(character <= 57 || character >= 65 && (character <= 90 || character >= 97));
        }
    }
}
