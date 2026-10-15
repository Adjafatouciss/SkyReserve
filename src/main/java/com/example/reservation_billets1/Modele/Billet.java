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
public class Billet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @NotBlank(message = "Numéro de billet obligatoire")
    @Column(nullable = false, length = 50, unique = true)
    @JsonProperty("numeroBillet")
    private String numeroBillet;

    @NotNull(message = "Prix du billet obligatoire")
    @Positive(message = "Le prix du billet doit être supérieur à 0")
    @Column(nullable = false)
    @JsonProperty("prixBillet")
    private Double prixBillet;

    @NotBlank(message = "Siège obligatoire")
    @Column(nullable = false, length = 20)
    @JsonProperty("siege")
    private String siege;

    @NotBlank(message = "Classe voyage obligatoire")
    @Column(nullable = false, length = 50)
    @JsonProperty("classeVoyage")
    private String classeVoyage;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    @JsonProperty("reservation")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "vol_id")
    @JsonProperty("vol")
    private Vol vol;
}