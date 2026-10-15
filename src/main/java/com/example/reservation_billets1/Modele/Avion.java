package com.example.reservation_billets1.Modele;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Avion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @NotBlank(message = "Modèle obligatoire")
    @Column(nullable = false, length = 100)
    @JsonProperty("modele")
    private String modele;

    @NotNull(message = "Capacité obligatoire")
    @Min(value = 1, message = "La capacité doit être supérieure à 0")
    @Column(nullable = false)
    @JsonProperty("capacite")
    private Integer capacite;

    @NotBlank(message = "Immatriculation obligatoire")
    @Column(nullable = false, length = 50, unique = true)
    @JsonProperty("immatriculation")
    private String immatriculation;

    @ManyToOne
    @JoinColumn(name = "compagnie_id")
    @JsonProperty("compagnieAerienne")
    private CompagnieAerienne compagnieAerienne;

    @OneToMany(mappedBy = "avion")
    @JsonIgnore
    private List<Vol> listeVols;
}