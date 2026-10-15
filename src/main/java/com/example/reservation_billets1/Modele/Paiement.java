package com.example.reservation_billets1.Modele;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @NotNull(message = "Montant obligatoire")
    @Positive(message = "Le montant doit être supérieur à 0")
    @Column(nullable = false)
    @JsonProperty("montant")
    private Double montant;

    @NotBlank(message = "Mode de paiement obligatoire")
    @Column(nullable = false, length = 50)
    @JsonProperty("modePaiement")
    private String modePaiement;

    @NotBlank(message = "Date de paiement obligatoire")
    @Column(nullable = false, length = 30)
    @JsonProperty("datePaiement")
    private String datePaiement;

    @NotBlank(message = "Statut paiement obligatoire")
    @Column(nullable = false, length = 50)
    @JsonProperty("statutPaiement")
    private String statutPaiement;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    @JsonProperty("reservation")
    private Reservation reservation;
}