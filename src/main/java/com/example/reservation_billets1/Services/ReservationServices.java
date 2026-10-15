package com.example.reservation_billets1.Services;

import com.example.reservation_billets1.Modele.Reservation;
import com.example.reservation_billets1.Modele.Utilisateur;
import com.example.reservation_billets1.Modele.Vol;
import com.example.reservation_billets1.Repository.ReservationRepository;
import com.example.reservation_billets1.Repository.UtilisateurRepository;
import com.example.reservation_billets1.Repository.VolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServices {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private VolRepository volRepository;

    public Reservation ajouterReservation(Reservation reservation) {
        hydraterRelations(reservation);
        return reservationRepository.save(reservation);
    }

    public boolean existeReservation(Long id) {
        return reservationRepository.existsById(id);
    }

    public void supprimerReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public Reservation modifierReservation(Long id, Reservation reservation) {
        Reservation existante = reservationRepository.findById(id).orElse(null);
        if (existante == null) {
            return null;
        }

        existante.setDateReservation(reservation.getDateReservation());
        existante.setStatutReservation(reservation.getStatutReservation());
        hydraterRelations(reservation);
        existante.setUtilisateur(reservation.getUtilisateur());
        existante.setVol(reservation.getVol());
        return reservationRepository.save(existante);
    }

    public List<Reservation> listeReservations() {
        return reservationRepository.findAll();
    }

    public Reservation rechercherReservation(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public List<Reservation> listeReservationsParUtilisateur(Long utilisateurId) {
        return reservationRepository.findByUtilisateurId(utilisateurId);
    }

    private void hydraterRelations(Reservation reservation) {
        reservation.setUtilisateur(resoudreUtilisateur(reservation));
        reservation.setVol(resoudreVol(reservation));
    }

    private Utilisateur resoudreUtilisateur(Reservation reservation) {
        if (reservation.getUtilisateur() == null || reservation.getUtilisateur().getId() == null) {
            throw new IllegalArgumentException("L'utilisateur est obligatoire.");
        }
        return utilisateurRepository.findById(reservation.getUtilisateur().getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
    }

    private Vol resoudreVol(Reservation reservation) {
        if (reservation.getVol() == null || reservation.getVol().getId() == null) {
            throw new IllegalArgumentException("Le vol est obligatoire.");
        }
        return volRepository.findById(reservation.getVol().getId())
                .orElseThrow(() -> new IllegalArgumentException("Vol introuvable."));
    }
}
