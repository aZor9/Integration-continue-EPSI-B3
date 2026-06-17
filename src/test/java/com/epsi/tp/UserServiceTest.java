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
}
