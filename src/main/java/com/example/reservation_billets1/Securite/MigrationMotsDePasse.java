package com.example.reservation_billets1.Securite;

import com.example.reservation_billets1.Modele.Admin;
import com.example.reservation_billets1.Modele.Utilisateur;
import com.example.reservation_billets1.Repository.AdminRepository;
import com.example.reservation_billets1.Repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Au démarrage, chiffre avec BCrypt les mots de passe encore enregistrés en clair
 * (comptes créés avant l'ajout de Spring Security). Les comptes existants continuent
 * donc de fonctionner avec leur mot de passe habituel.
 */
@Component
@Order(1)
public class MigrationMotsDePasse implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MigrationMotsDePasse.class);

    private final UtilisateurRepository utilisateurs;
    private final AdminRepository admins;
    private final PasswordEncoder passwordEncoder;

    public MigrationMotsDePasse(UtilisateurRepository utilisateurs,
                                AdminRepository admins,
                                PasswordEncoder passwordEncoder) {
        this.utilisateurs = utilisateurs;
        this.admins = admins;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        int convertis = 0;

        for (Utilisateur u : utilisateurs.findAll()) {
            if (estEnClair(u.getMotDePasse())) {
                u.setMotDePasse(passwordEncoder.encode(u.getMotDePasse()));
                utilisateurs.save(u);
                convertis++;
            }
        }

        for (Admin a : admins.findAll()) {
            if (estEnClair(a.getMotDePasse())) {
                a.setMotDePasse(passwordEncoder.encode(a.getMotDePasse()));
                admins.save(a);
                convertis++;
            }
        }

        if (convertis > 0) {
            log.info("{} mot(s) de passe chiffré(s) avec BCrypt", convertis);
        }
    }

    /** Un mot de passe BCrypt commence toujours par $2a$, $2b$ ou $2y$. */
    private boolean estEnClair(String motDePasse) {
        return motDePasse != null && !motDePasse.matches("^\\$2[aby]\\$.*");
    }
}
