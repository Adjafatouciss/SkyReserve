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
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @NotBlank(message = "Date de réservation obligatoire")
    @Column(nullable = false, length = 30)
    @JsonProperty("dateReservation")
    private String dateReservation;

    @NotBlank(message = "Statut réservation obligatoire")
    @Column(nullable = false, length = 50)
    @JsonProperty("statutReservation")
    private String statutReservation;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    @JsonProperty("utilisateur")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "vol_id")
    @JsonProperty("vol")
    private Vol vol;

    @OneToMany(mappedBy = "reservation")
    @JsonIgnore
    private List<Billet> listeBillets;

    @OneToOne(mappedBy = "reservation")
    @JsonIgnore
    private Paiement paiement;
}