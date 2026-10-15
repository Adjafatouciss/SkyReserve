package com.example.reservation_billets1.Services;

import com.example.reservation_billets1.Modele.Billet;
import com.example.reservation_billets1.Modele.Reservation;
import com.example.reservation_billets1.Modele.Vol;
import com.example.reservation_billets1.Repository.BilletRepository;
import com.example.reservation_billets1.Repository.ReservationRepository;
import com.example.reservation_billets1.Repository.VolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BilletServices {

    @Autowired
    private BilletRepository billetRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private VolRepository volRepository;

    public Billet ajouterBillet(Billet billet) {
        hydraterRelations(billet);
        return billetRepository.save(billet);
    }

    public boolean existeBillet(Long id) {
        return billetRepository.existsById(id);
    }

    public void supprimerBillet(Long id) {
        billetRepository.deleteById(id);
    }

    public Billet modifierBillet(Long id, Billet billet) {
        Billet existant = billetRepository.findById(id).orElse(null);
        if (existant == null) {
            return null;
        }

        existant.setNumeroBillet(billet.getNumeroBillet());
        existant.setPrixBillet(billet.getPrixBillet());
        existant.setSiege(billet.getSiege());
        existant.setClasseVoyage(billet.getClasseVoyage());
        hydraterRelations(billet);
        existant.setReservation(billet.getReservation());
        existant.setVol(billet.getVol());
        return billetRepository.save(existant);
    }

    public List<Billet> listeBillets() {
        return billetRepository.findAll();
    }

    public Billet rechercherBillet(Long id) {
        return billetRepository.findById(id).orElse(null);
    }

    private void hydraterRelations(Billet billet) {
        billet.setReservation(resoudreReservation(billet));
        billet.setVol(resoudreVol(billet));
    }

    private Reservation resoudreReservation(Billet billet) {
        if (billet.getReservation() == null || billet.getReservation().getId() == null) {
            throw new IllegalArgumentException("La réservation est obligatoire.");
        }
        return reservationRepository.findById(billet.getReservation().getId())
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable."));
    }

    private Vol resoudreVol(Billet billet) {
        if (billet.getVol() == null || billet.getVol().getId() == null) {
            throw new IllegalArgumentException("Le vol est obligatoire.");
        }
        return volRepository.findById(billet.getVol().getId())
                .orElseThrow(() -> new IllegalArgumentException("Vol introuvable."));
    }
}
