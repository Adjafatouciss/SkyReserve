package com.example.reservation_billets1.Controlleur;

import com.example.reservation_billets1.Modele.Paiement;
import com.example.reservation_billets1.Modele.Reservation;
import com.example.reservation_billets1.Securite.SessionUtilisateur;
import com.example.reservation_billets1.Services.PaiementServices;
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
public class PaiementControlleur {

    @Autowired
    private PaiementServices paiementServices;

    @Autowired
    private ReservationServices reservationServices;

    // Session de la personne connectée (Spring Security)
    @Autowired
    private SessionUtilisateur session;

    /** Id de l'utilisateur propriétaire de la réservation liée (null si aucun). */
    private Long proprietaire(Paiement element) {
        if (element == null || element.getReservation() == null) {
            return null;
        }
        Reservation r = element.getReservation();
        if (r.getUtilisateur() == null && r.getId() != null) {
            // Seul l'id de la réservation est envoyé : on la recharge depuis la base
            r = reservationServices.rechercherReservation(r.getId());
        }
        return (r == null || r.getUtilisateur() == null) ? null : r.getUtilisateur().getId();
    }

    @PostMapping("/ajouterPaiement")
    public ResponseEntity<?> ajouterPaiement(@Valid @RequestBody Paiement paiement, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        // Un utilisateur ne peut payer / générer un billet que pour SES réservations
        if (!session.peutAcceder(proprietaire(paiement))) {
            return ResponseEntity.status(403).body("Accès refusé");
        }
        try {
            return ResponseEntity.status(201).body(paiementServices.ajouterPaiement(paiement));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/supprimerPaiement/{id}")
    public ResponseEntity<?> supprimerPaiement(@PathVariable Long id) {
        if (!paiementServices.existePaiement(id)) {
            return ResponseEntity.status(404).body("Paiement non trouvé");
        }
        paiementServices.supprimerPaiement(id);
        return ResponseEntity.ok("Paiement supprimé avec succès");
    }

    @PutMapping("/modifierPaiement/{id}")
    public ResponseEntity<?> modifierPaiement(@PathVariable Long id,
                                              @Valid @RequestBody Paiement paiement,
                                              BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try {
            Paiement updated = paiementServices.modifierPaiement(id, paiement);
            if (updated == null) {
                return ResponseEntity.status(404).body("Paiement non trouvé");
            }
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/afficherTousLesPaiements")
    public ResponseEntity<?> afficherTousLesPaiements() {
        List<Paiement> liste = paiementServices.listePaiements();
        // Un utilisateur ne reçoit que ses propres données ; un admin reçoit tout
        if (!session.estAdmin()) {
            liste = liste.stream()
                    .filter(element -> session.peutAcceder(proprietaire(element)))
                    .toList();
        }
        if (liste.isEmpty()) {
            return ResponseEntity.status(404).body("Aucun paiement trouvé");
        }
        return ResponseEntity.ok(liste);
    }

    @GetMapping("/paiement/{id}")
    public ResponseEntity<?> getPaiementParId(@PathVariable Long id) {
        Paiement paiement = paiementServices.rechercherPaiement(id);
        if (paiement == null) {
            return ResponseEntity.status(404).body("Paiement non trouvé");
        }
        if (!session.peutAcceder(proprietaire(paiement))) {
            return ResponseEntity.status(403).body("Accès refusé");
        }
        return ResponseEntity.ok(paiement);
    }
}
