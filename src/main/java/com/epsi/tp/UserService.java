package com.epsi.tp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    private String userPassword = System.getenv("USER_PASSWORD");
    private String userUsername = System.getenv("USER_USERNAME");
    private String dbUrl = System.getenv("DB_URL");
    private String dbPassword = System.getenv("DB_PASSWORD");
    private String dbUsername = System.getenv("DB_USERNAME");
    public void login(String username, String password) {
        
        LoggerUtil.info("Tentative de connexion de l'utilisateur : " + username);

        if (username.equals(userUsername) && password.equals(userPassword)) {
            LoggerUtil.info("Administrateur connecté avec succès.");
        } else {
            LoggerUtil.warning("Identifiants invalides.");
        }
        
        try {
            // Logique factice pour déclencher une exception
            int result = 10 / 0;
        } catch (Exception e) {
            LoggerUtil.warning("Erreur lors de l'exception factice : " + e.getMessage());
        }
    }

    public void getUserDetails(String username) {
    String query = "SELECT username FROM users WHERE username = ?";

    try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, username);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                LoggerUtil.info("Utilisateur trouvé : " + rs.getString("username"));
            }
        }

    } catch (SQLException e) {
        LoggerUtil.error("Erreur lors de la récupération des détails utilisateur", e);
    }
}
    
    public void complexMethod(int a, int b, int c) {
        if (a <= 0) {
            LoggerUtil.info("A est négatif");
        } else if (b <= 0 && c <= 0) {
            LoggerUtil.info("B et C sont négatifs");
        } else if (b <= 0) {
            LoggerUtil.info("B est négatif");
        } else if (c <= 0) {
            LoggerUtil.info("C est négatif");
        } else {
            LoggerUtil.info("Tous positifs");
        }
    }
}
