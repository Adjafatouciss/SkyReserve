package com.example.reservation_billets1.Modele;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    public static final String ROLE_ADMIN = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @NotBlank(message = "Nom obligatoire")
    @Column(nullable = false, length = 50)
    @JsonProperty("nom")
    private String nom;

    @NotBlank(message = "Prénom obligatoire")
    @Column(nullable = false, length = 50)
    @JsonProperty("prenom")
    private String prenom;

    @Email(message = "Email invalide")
    @NotBlank(message = "Email obligatoire")
    @Column(nullable = false, length = 100, unique = true)
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Mot de passe obligatoire")
    @Column(nullable = false, length = 100)
    @JsonProperty("motDePasse")
    private String motDePasse;

    @Column(length = 30)
    @JsonProperty("role")
    private String role;

    @PrePersist
    @PreUpdate
    public void appliquerRoleParDefaut() {
        if (role == null || role.isBlank()) {
            role = ROLE_ADMIN;
        }
    }
}
