package com.epsi.tp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testMain() {
        // Vérifie que le point d'entrée s'exécute sans lever d'exception.
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}