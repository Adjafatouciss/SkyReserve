package com.example.reservation_billets1.Modele;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aeroport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @NotBlank(message = "Nom de l'aéroport obligatoire")
    @Column(nullable = false, length = 100)
    @JsonProperty("nomAeroport")
    private String nomAeroport;

    @NotBlank(message = "Ville obligatoire")
    @Column(nullable = false, length = 100)
    @JsonProperty("ville")
    private String ville;

    @NotBlank(message = "Pays obligatoire")
    @Column(nullable = false, length = 100)
    @JsonProperty("pays")
    private String pays;

    @NotBlank(message = "Code aéroport obligatoire")
    @Column(nullable = false, length = 20, unique = true)
    @JsonProperty("codeAeroport")
    private String codeAeroport;

    @OneToMany(mappedBy = "aeroportDepart")
    @JsonIgnore
    private List<Vol> listeVolsDepart;

    @OneToMany(mappedBy = "aeroportArrivee")
    @JsonIgnore
    private List<Vol> listeVolsArrivee;
}