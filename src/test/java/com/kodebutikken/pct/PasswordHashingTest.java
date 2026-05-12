package com.kodebutikken.pct;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordHashingTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Test
    void encodedPasswordMatcher_RawPassword() {
        String rawPassword = "hemmelig123";
        String hash = passwordEncoder.encode(rawPassword);

        assertTrue(passwordEncoder.matches(rawPassword, hash));
    }

    @Test
    void twoDifferentPasswords_DifferentHashes() {
        String rawPassword = "hemmelig123";

        String hash1 = passwordEncoder.encode(rawPassword);
        String hash2 = passwordEncoder.encode(rawPassword);

        assertNotEquals(hash1, hash2);
    }

    @Test
    void wrongPassword_doesNotMatch() {
        String hash = passwordEncoder.encode("rigtigPassword");

        assertFalse(passwordEncoder.matches("forkertPassword", hash));
    }

    @Test
    void hashedPassword_isNotStoredAsPlainText() {
        String rawPassword = "hemmelig123";
        String hash = passwordEncoder.encode(rawPassword);

        assertNotEquals(rawPassword, hash);
    }
}
