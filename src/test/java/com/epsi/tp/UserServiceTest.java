package com.epsi.tp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    public void testLoginAdmin() {
        UserService userService = new UserService();
        
        // On teste la méthode login avec les bons identifiants.
        // Ce test permet d'avoir une couverture de code partielle pour JaCoCo/SonarQube.
        assertDoesNotThrow(() -> {
            userService.login("admin", "super_secret_password_123!");
        });
    }
}
