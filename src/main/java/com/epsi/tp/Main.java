package com.epsi.tp;

public class Main {
    public static void main(String[] args) {
        LoggerUtil.info("Démarrage de l'application...");
        
        UserService userService = new UserService();
        userService.login("admin", "super_secret_password_123!");
        userService.getUserDetails("admin");
    }
}
