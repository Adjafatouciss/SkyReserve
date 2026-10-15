package com.example.reservation_billets1.Modele;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @NotBlank(message = "Numéro du vol obligatoire")
    @Column(nullable = false, length = 50, unique = true)
    @JsonProperty("numeroVol")
    private String numeroVol;

    @NotBlank(message = "Date de départ obligatoire")
    @Column(nullable = false, length = 30)
    @JsonProperty("dateDepart")
    private String dateDepart;

    @NotBlank(message = "Heure de départ obligatoire")
    @Column(nullable = false, length = 20)
    @JsonProperty("heureDepart")
    private String heureDepart;

    @NotBlank(message = "Date d'arrivée obligatoire")
    @Column(nullable = false, length = 30)
    @JsonProperty("dateArrivee")
    private String dateArrivee;

    @NotBlank(message = "Heure d'arrivée obligatoire")
    @Column(nullable = false, length = 20)
    @JsonProperty("heureArrivee")
    private String heureArrivee;

    @NotNull(message = "Prix obligatoire")
    @Positive(message = "Le prix doit être supérieur à 0")
    @Column(nullable = false)
    @JsonProperty("prix")
    private Double prix;

    @ManyToOne
    @JoinColumn(name = "compagnie_id")
    @JsonProperty("compagnieAerienne")
    private CompagnieAerienne compagnieAerienne;

    @ManyToOne
    @JoinColumn(name = "avion_id")
    @JsonProperty("avion")
    private Avion avion;

    @ManyToOne
    @JoinColumn(name = "aeroport_depart_id")
    @JsonProperty("aeroportDepart")
    private Aeroport aeroportDepart;

    @ManyToOne
    @JoinColumn(name = "aeroport_arrivee_id")
    @JsonProperty("aeroportArrivee")
    private Aeroport aeroportArrivee;

    @OneToMany(mappedBy = "vol")
    @JsonIgnore
    private List<Reservation> listeReservations;

    @OneToMany(mappedBy = "vol")
    @JsonIgnore
    private List<Billet> listeBillets;
}