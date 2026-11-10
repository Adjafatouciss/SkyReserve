package com.example.reservation_billets1.Securite;

import java.io.Serializable;

/**
 * La personne connectée, gardée dans la session : son id, son courriel et son rôle
 * ("UTILISATEUR" ou "ADMIN"). Aucun mot de passe n'est stocké ici.
 */
public record CompteConnecte(Long id, String email, String role) implements Serializable {

    public boolean estAdmin() {
        return "ADMIN".equals(role);
    }
}
