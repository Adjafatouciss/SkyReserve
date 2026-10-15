package com.example.reservation_billets1.Services;

import com.example.reservation_billets1.Modele.Paiement;
import com.example.reservation_billets1.Modele.Reservation;
import com.example.reservation_billets1.Repository.PaiementRepository;
import com.example.reservation_billets1.Repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaiementServices {

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public Paiement ajouterPaiement(Paiement paiement) {
        paiement.setReservation(resoudreReservation(paiement));
        return paiementRepository.save(paiement);
    }

    public boolean existePaiement(Long id) {
        return paiementRepository.existsById(id);
    }

    public void supprimerPaiement(Long id) {
        paiementRepository.deleteById(id);
    }

    public Paiement modifierPaiement(Long id, Paiement paiement) {
        Paiement existant = paiementRepository.findById(id).orElse(null);
        if (existant == null) {
            return null;
        }

        existant.setMontant(paiement.getMontant());
        existant.setModePaiement(paiement.getModePaiement());
        existant.setDatePaiement(paiement.getDatePaiement());
        existant.setStatutPaiement(paiement.getStatutPaiement());
        existant.setReservation(resoudreReservation(paiement));
        return paiementRepository.save(existant);
    }

    public List<Paiement> listePaiements() {
        return paiementRepository.findAll();
    }

    public Paiement rechercherPaiement(Long id) {
        return paiementRepository.findById(id).orElse(null);
    }

    private Reservation resoudreReservation(Paiement paiement) {
        if (paiement.getReservation() == null || paiement.getReservation().getId() == null) {
            throw new IllegalArgumentException("La réservation est obligatoire.");
        }
        return reservationRepository.findById(paiement.getReservation().getId())
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable."));
    }
}
