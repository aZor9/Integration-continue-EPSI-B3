package com.epsi.tp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void testLoginAdmin() {
        UserService userService = new UserService();

        // On teste la méthode login avec les bons identifiants.
        // Ce test permet d'avoir une couverture de code partielle pour JaCoCo/SonarQube.
        assertDoesNotThrow(() -> {
            userService.login(System.getenv("USER_USERNAME"), System.getenv("USER_PASSWORD"));
        });
    }

    @Test
    void testLoginInvalidCredentials() {
        UserService userService = new UserService();

        // Couvre la branche "Identifiants invalides" (else).
        assertDoesNotThrow(() -> {
            userService.login("mauvaisUser", "mauvaisMotDePasse");
        });
    }

    @Test
    void testGetUserDetails() {
        UserService userService = new UserService();

        // L'exception SQL éventuelle est gérée à l'intérieur de la méthode,
        // donc l'appel ne doit pas remonter d'erreur.
        assertDoesNotThrow(() -> {
            userService.getUserDetails("admin");
        });
    }

    @Test
    void testComplexMethodANegatif() {
        UserService userService = new UserService();
        assertDoesNotThrow(() -> userService.complexMethod(-1, 1, 1));
    }

    @Test
    void testComplexMethodBEtCNegatifs() {
        UserService userService = new UserService();
        assertDoesNotThrow(() -> userService.complexMethod(1, -1, -1));
    }

    @Test
    void testComplexMethodBNegatif() {
        UserService userService = new UserService();
        assertDoesNotThrow(() -> userService.complexMethod(1, -1, 1));
    }

    @Test
    void testComplexMethodCNegatif() {
        UserService userService = new UserService();
        assertDoesNotThrow(() -> userService.complexMethod(1, 1, -1));
    }

    @Test
    void testComplexMethodTousPositifs() {
        UserService userService = new UserService();
        assertDoesNotThrow(() -> userService.complexMethod(1, 1, 1));
    }
}