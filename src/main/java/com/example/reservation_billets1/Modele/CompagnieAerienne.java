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
public class CompagnieAerienne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @NotBlank(message = "Nom de la compagnie obligatoire")
    @Column(nullable = false, length = 100, unique = true)
    @JsonProperty("nomCompagnie")
    private String nomCompagnie;

    @NotBlank(message = "Pays obligatoire")
    @Column(nullable = false, length = 100)
    @JsonProperty("pays")
    private String pays;

    @NotBlank(message = "Code compagnie obligatoire")
    @Column(nullable = false, length = 20, unique = true)
    @JsonProperty("codeCompagnie")
    private String codeCompagnie;

    @OneToMany(mappedBy = "compagnieAerienne")
    @JsonIgnore
    private List<Avion> listeAvions;

    @OneToMany(mappedBy = "compagnieAerienne")
    @JsonIgnore
    private List<Vol> listeVols;
}