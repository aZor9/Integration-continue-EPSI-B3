package com.epsi.tp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserService {

    private String USER_PASSWORD = System.getenv("USER_PASSWORD");
    private String USER_USERNAME = System.getenv("USER_USERNAME");
    private String DB_URL = System.getenv("DB_URL");
    private String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private String DB_USERNAME = System.getenv("DB_USERNAME");

    public void login(String username, String password) {
        
        LoggerUtil.info("Tentative de connexion de l'utilisateur : " + username);

        if (username.equals(USER_USERNAME) && password.equals(USER_PASSWORD)) {
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
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.createStatement();
            
            // Faille de sécurité majeure : Injection SQL possible via concaténation
            String query = "SELECT * FROM users WHERE username = '" + username + "'";
            rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                LoggerUtil.info("Utilisateur trouvé : " + rs.getString("username"));
            }
        } catch (Exception e) {
            LoggerUtil.error("Erreur lors de la récupération des détails utilisateur", e);
        } finally {
            // Mauvaise pratique : gestion archaïque des ressources (pas de try-with-resources)
            // avec des catch vides
            if (rs != null) {
                try { rs.close(); } catch (Exception e) {}
            }
            if (stmt != null) {
                try { stmt.close(); } catch (Exception e) {}
            }
            if (conn != null) {
                try { conn.close(); } catch (Exception e) {}
            }
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
