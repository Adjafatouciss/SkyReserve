package com.example.reservation_billets1.Securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * Configuration de Spring Security.
 *
 * - Les pages HTML, le CSS et le JavaScript restent publics.
 * - L'API est protégée : chaque route est ouverte à tous, aux utilisateurs connectés
 *   ou seulement aux administrateurs (voir la liste ci-dessous).
 * - La connexion est gardée dans une session côté serveur (cookie JSESSIONID).
 * - La protection CSRF empêche un autre site d'envoyer des requêtes à la place
 *   de l'utilisateur connecté (le jeton est lu dans le cookie XSRF-TOKEN par config.js).
 */
@Configuration
@EnableWebSecurity
public class SecuriteConfig {

    private static final String API = "/api/reservationBillets";

    /** Chiffrement des mots de passe avec BCrypt. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Garde l'utilisateur connecté dans sa session HTTP. */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   SecurityContextRepository contextRepository) throws Exception {
        http
            .securityContext(context -> context.securityContextRepository(contextRepository))

            // Jeton CSRF dans un cookie lisible par JavaScript, renvoyé dans l'en-tête X-XSRF-TOKEN
            .csrf(csrf -> csrf.spa())

            .authorizeHttpRequests(acces -> acces
                // --- Ouvert à tous ---
                .requestMatchers(HttpMethod.POST,
                        API + "/loginUtilisateur",
                        API + "/loginAdmin",
                        API + "/ajouterUtilisateur",      // inscription
                        API + "/logout").permitAll()
                .requestMatchers(HttpMethod.GET,
                        API + "/vols",
                        API + "/afficherTousLesVols",
                        API + "/vol/*").permitAll()

                // --- Utilisateur connecté (le contrôleur vérifie qu'il s'agit de SES données) ---
                .requestMatchers(HttpMethod.GET,
                        API + "/utilisateur/*",
                        API + "/reservations/utilisateur/*",
                        API + "/reservation/*",
                        API + "/afficherTousLesBillets",
                        API + "/billet/*",
                        API + "/afficherTousLesPaiements",
                        API + "/paiement/*").authenticated()
                .requestMatchers(HttpMethod.POST,
                        API + "/ajouterReservation",
                        API + "/ajouterBillet",
                        API + "/ajouterPaiement").authenticated()
                .requestMatchers(HttpMethod.PUT,
                        API + "/modifierUtilisateur/*",
                        API + "/modifierReservation/*").authenticated()

                // --- Tout le reste de l'API : administrateurs seulement ---
                .requestMatchers(API + "/**").hasRole("ADMIN")

                // --- Pages HTML, CSS, JavaScript, images ---
                .anyRequest().permitAll()
            )

            // Pas connecté : on répond 401 (l'API ne redirige pas vers un formulaire)
            .exceptionHandling(erreurs -> erreurs
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

            // La connexion se fait par nos propres routes /loginUtilisateur et /loginAdmin
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // Déconnexion : POST /api/reservationBillets/logout, détruit la session
            .logout(logout -> logout
                .logoutUrl(API + "/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)));

        return http.build();
    }
}
