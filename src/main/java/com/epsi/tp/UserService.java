package com.epsi.tp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserService {

    private String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private String DB_USERNAME = System.getenv("DB_USERNAME");
    private String DB_URL = System.getenv("DB_URL");

    public void login(String username, String password) {
        
        LoggerUtil.info("Tentative de connexion de l'utilisateur : " + username);

        if (username.equals(DB_USERNAME) && password.equals(DB_PASSWORD)) {
            LoggerUtil.info("Administrateur connecté avec succès.");
        } else {
            LoggerUtil.warning("Identifiants invalides.");
        }
        
        try {
            // Logique factice pour déclencher une exception
            int result = 10 / 0;
        } catch (Exception e) {
            // Mauvaise pratique : bloc catch vide (l'erreur est ignorée silencieusement)
        }
    }

    public void getUserDetails(String username) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "root", DB_PASSWORD);
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
    
    // Mauvaise pratique : méthode inutilement complexe avec de nombreux "if" imbriqués (complexité cyclomatique élevée)
    public void complexMethod(int a, int b, int c) {
        if (a > 0) {
            if (b > 0) {
                if (c > 0) {
                    LoggerUtil.info("Tous positifs");
                } else {
                    LoggerUtil.info("C est négatif");
                }
            } else {
                if (c > 0) {
                    LoggerUtil.info("B est négatif");
                } else {
                    LoggerUtil.info("B et C sont négatifs");
                }
            }
        } else {
            LoggerUtil.info("A est négatif");
        }
    }
}
