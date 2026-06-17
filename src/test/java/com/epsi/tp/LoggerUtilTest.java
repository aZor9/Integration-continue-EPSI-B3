package com.epsi.tp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoggerUtilTest {

    @Test
    void testInfo() {
        assertDoesNotThrow(() -> LoggerUtil.info("Message d'information"));
    }

    @Test
    void testWarning() {
        assertDoesNotThrow(() -> LoggerUtil.warning("Message d'avertissement"));
    }

    @Test
    void testSevere() {
        assertDoesNotThrow(() -> LoggerUtil.severe("Message critique"));
    }

    @Test
    void testError() {
        assertDoesNotThrow(() -> LoggerUtil.error("Message d'erreur", new Exception("Exception de test")));
    }
}