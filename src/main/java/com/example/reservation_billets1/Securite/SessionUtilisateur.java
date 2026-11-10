package com.example.reservation_billets1.Securite;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Outils autour de la personne connectée :
 * - ouvrir la session après une connexion réussie ;
 * - savoir qui est connecté ;
 * - vérifier qu'un utilisateur ne touche qu'à SES données (un admin a accès à tout).
 */
@Component
public class SessionUtilisateur {

    private final SecurityContextRepository contextRepository;

    public SessionUtilisateur(SecurityContextRepository contextRepository) {
        this.contextRepository = contextRepository;
    }

    /** Enregistre la personne connectée dans sa session. */
    public void connecter(Long id, String email, String role,
                          HttpServletRequest request, HttpServletResponse response) {
        // Nouvel identifiant de session à chaque connexion (protection contre la « fixation de session »)
        if (request.getSession(false) != null) {
            request.changeSessionId();
        }

        CompteConnecte compte = new CompteConnecte(id, email, role);
        Authentication authentification = UsernamePasswordAuthenticationToken.authenticated(
                compte, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentification);
        SecurityContextHolder.setContext(context);
        contextRepository.saveContext(context, request, response);
    }

    /** La personne connectée, ou null si personne n'est connecté. */
    public CompteConnecte compte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CompteConnecte compte) {
            return compte;
        }
        return null;
    }

    public boolean estAdmin() {
        CompteConnecte compte = compte();
        return compte != null && compte.estAdmin();
    }

    /** Vrai si la personne connectée est cet utilisateur, ou un administrateur. */
    public boolean peutAcceder(Long utilisateurId) {
        CompteConnecte compte = compte();
        if (compte == null) {
            return false;
        }
        return compte.estAdmin()
                || ("UTILISATEUR".equals(compte.role()) && Objects.equals(compte.id(), utilisateurId));
    }
}
