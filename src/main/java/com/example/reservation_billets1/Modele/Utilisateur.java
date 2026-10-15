package com.example.reservation_billets1.Modele;

// Permet de contrôler le nom des champs dans le JSON (API)
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

// Annotations JPA pour la base de données
import jakarta.persistence.*;

// Annotations de validation (vérifier les champs)
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Lombok pour éviter d’écrire le code répétitif
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// @Entity → dit à Spring que cette classe représente une table dans la base de données
@Entity

// @Data → génère automatiquement getters, setters, toString, equals, hashCode
@Data

// @NoArgsConstructor → crée un constructeur vide
@NoArgsConstructor

// @AllArgsConstructor → crée un constructeur avec tous les attributs
@AllArgsConstructor
public class Utilisateur {

    public static final String ROLE_UTILISATEUR = "UTILISATEUR";

    // @Id → clé primaire de la table
    // @GeneratedValue → auto-incrémentation (1,2,3,...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // @JsonProperty → nom du champ dans le JSON
    @JsonProperty("id")
    private Long id;

    // @NotBlank → interdit null ou vide ("")
    @NotBlank(message = "Nom obligatoire")

    // @Column → configuration colonne DB
    // nullable = false → obligatoire
    // length → taille max
    @Column(nullable = false, length = 50)

    // Nom dans JSON
    @JsonProperty("nom")
    private String nom;

    @NotBlank(message = "Prénom obligatoire")
    @Column(nullable = false, length = 50)
    @JsonProperty("prenom")
    private String prenom;

    // @Email → vérifie format email
    @Email(message = "Email invalide")

    @NotBlank(message = "Email obligatoire")
    @Column(nullable = false, length = 100, unique = true)
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Mot de passe obligatoire")
    @Column(nullable = false, length = 100)
    @JsonProperty("motDePasse")
    private String motDePasse;

    // Champ optionnel (pas obligatoire)
    @Column(length = 20)
    @JsonProperty("telephone")
    private String telephone;

    @Column(length = 30)
    @JsonProperty("role")
    private String role;

    @PrePersist
    @PreUpdate
    public void appliquerRoleParDefaut() {
        if (role == null || role.isBlank()) {
            role = ROLE_UTILISATEUR;
        }
    }

    // RELATION :
    // Un utilisateur peut avoir plusieurs réservations
    // @OneToMany → 1 utilisateur → plusieurs réservations
    // mappedBy = "utilisateur" → correspond au champ dans Reservation
    @OneToMany(mappedBy = "utilisateur")

    // @JsonIgnore → évite les boucles infinies JSON
    @JsonIgnore
    private List<Reservation> reservations;
}