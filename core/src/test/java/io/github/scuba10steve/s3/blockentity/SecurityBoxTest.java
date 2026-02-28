package io.github.scuba10steve.s3.blockentity;

import io.github.scuba10steve.s3.blockentity.SecurityBoxBlockEntity.SecurePlayer;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SecurePlayer record and pure-logic security methods.
 */
class SecurityBoxTest {

    @Test
    void testSecurePlayerConstruction() {
        UUID id = UUID.randomUUID();
        String name = "TestPlayer";
        SecurePlayer player = new SecurePlayer(id, name);

        assertEquals(id, player.id());
        assertEquals(name, player.name());
    }

    @Test
    void testSecurePlayerEquality() {
        UUID id = UUID.randomUUID();
        SecurePlayer player1 = new SecurePlayer(id, "TestPlayer");
        SecurePlayer player2 = new SecurePlayer(id, "TestPlayer");

        assertEquals(player1, player2);
        assertEquals(player1.hashCode(), player2.hashCode());
    }

    @Test
    void testSecurePlayerInequalityDifferentId() {
        SecurePlayer player1 = new SecurePlayer(UUID.randomUUID(), "TestPlayer");
        SecurePlayer player2 = new SecurePlayer(UUID.randomUUID(), "TestPlayer");

        assertNotEquals(player1, player2);
    }

    @Test
    void testSecurePlayerInequalityDifferentName() {
        UUID id = UUID.randomUUID();
        SecurePlayer player1 = new SecurePlayer(id, "Player1");
        SecurePlayer player2 = new SecurePlayer(id, "Player2");

        // Records compare all fields, so different names means not equal
        assertNotEquals(player1, player2);
    }

    @Test
    void testSecurePlayerNullSafety() {
        // UUID should not be null in practice, but name could be empty
        UUID id = UUID.randomUUID();
        SecurePlayer player = new SecurePlayer(id, "");
        assertEquals("", player.name());
        assertNotNull(player.id());
    }
}
