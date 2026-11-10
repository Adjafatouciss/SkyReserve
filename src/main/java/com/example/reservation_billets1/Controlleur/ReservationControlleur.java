package com.example.reservation_billets1.Controlleur;

import com.example.reservation_billets1.Modele.Reservation;
import com.example.reservation_billets1.Securite.SessionUtilisateur;
import com.example.reservation_billets1.Services.ReservationServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservationBillets")
@CrossOrigin(origins = "*")
public class ReservationControlleur {

    @Autowired
    private ReservationServices reservationServices;

    // Session de la personne connectée (Spring Security)
    @Autowired
    private SessionUtilisateur session;

    /** Id de l'utilisateur à qui appartient la réservation (null si aucun). */
    private Long proprietaire(Reservation r) {
        return (r == null || r.getUtilisateur() == null) ? null : r.getUtilisateur().getId();
    }

    @PostMapping("/ajouterReservation")
    public ResponseEntity<?> ajouterReservation(@Valid @RequestBody Reservation reservation, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        // Un utilisateur ne peut réserver qu'à son propre nom
        if (!session.peutAcceder(proprietaire(reservation))) {
            return ResponseEntity.status(403).body("Accès refusé");
        }
        try {
            return ResponseEntity.status(201).body(reservationServices.ajouterReservation(reservation));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/supprimerReservation/{id}")
    public ResponseEntity<?> supprimerReservation(@PathVariable Long id) {
        if (!reservationServices.existeReservation(id)) {
            return ResponseEntity.status(404).body("Réservation non trouvée");
        }
        reservationServices.supprimerReservation(id);
        return ResponseEntity.ok("Réservation supprimée avec succès");
    }

    @PutMapping("/modifierReservation/{id}")
    public ResponseEntity<?> modifierReservation(@PathVariable Long id,
                                                 @Valid @RequestBody Reservation reservation,
                                                 BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        // Un utilisateur ne peut modifier (annuler) que ses propres réservations
        Reservation existante = reservationServices.rechercherReservation(id);
        if (existante != null && (!session.peutAcceder(proprietaire(existante))
                || !session.peutAcceder(proprietaire(reservation)))) {
            return ResponseEntity.status(403).body("Accès refusé");
        }
        try {
            Reservation updated = reservationServices.modifierReservation(id, reservation);
            if (updated == null) {
                return ResponseEntity.status(404).body("Réservation non trouvée");
            }
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/afficherToutesLesReservations")
    public ResponseEntity<?> afficherToutesLesReservations() {
        List<Reservation> liste = reservationServices.listeReservations();
        if (liste.isEmpty()) {
            return ResponseEntity.status(404).body("Aucune réservation trouvée");
        }
        return ResponseEntity.ok(liste);
    }

    @GetMapping("/reservation/{id}")
    public ResponseEntity<?> getReservationParId(@PathVariable Long id) {
        Reservation reservation = reservationServices.rechercherReservation(id);
        if (reservation == null) {
            return ResponseEntity.status(404).body("Réservation non trouvée");
        }
        if (!session.peutAcceder(proprietaire(reservation))) {
            return ResponseEntity.status(403).body("Accès refusé");
        }
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/reservations/utilisateur/{id}")
    public ResponseEntity<?> getReservationsByUtilisateur(@PathVariable Long id) {
        if (!session.peutAcceder(id)) {
            return ResponseEntity.status(403).body("Accès refusé");
        }
        List<Reservation> reservations = reservationServices.listeReservationsParUtilisateur(id);
        return ResponseEntity.ok(reservations); // renvoie une liste vide si aucune réservation
    }
}
