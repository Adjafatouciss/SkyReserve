package com.example.reservation_billets1.Controlleur;

import com.example.reservation_billets1.Modele.Billet;
import com.example.reservation_billets1.Modele.Reservation;
import com.example.reservation_billets1.Securite.SessionUtilisateur;
import com.example.reservation_billets1.Services.BilletServices;
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
public class BilletControlleur {

    @Autowired
    private BilletServices billetServices;

    @Autowired
    private ReservationServices reservationServices;

    // Session de la personne connectée (Spring Security)
    @Autowired
    private SessionUtilisateur session;

    /** Id de l'utilisateur propriétaire de la réservation liée (null si aucun). */
    private Long proprietaire(Billet element) {
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

    @PostMapping("/ajouterBillet")
    public ResponseEntity<?> ajouterBillet(@Valid @RequestBody Billet billet, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        // Un utilisateur ne peut payer / générer un billet que pour SES réservations
        if (!session.peutAcceder(proprietaire(billet))) {
            return ResponseEntity.status(403).body("Accès refusé");
        }
        try {
            return ResponseEntity.status(201).body(billetServices.ajouterBillet(billet));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/supprimerBillet/{id}")
    public ResponseEntity<?> supprimerBillet(@PathVariable Long id) {
        if (!billetServices.existeBillet(id)) {
            return ResponseEntity.status(404).body("Billet non trouvé");
        }
        billetServices.supprimerBillet(id);
        return ResponseEntity.ok("Billet supprimé avec succès");
    }

    @PutMapping("/modifierBillet/{id}")
    public ResponseEntity<?> modifierBillet(@PathVariable Long id,
                                            @Valid @RequestBody Billet billet,
                                            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try {
            Billet updated = billetServices.modifierBillet(id, billet);
            if (updated == null) {
                return ResponseEntity.status(404).body("Billet non trouvé");
            }
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/afficherTousLesBillets")
    public ResponseEntity<?> afficherTousLesBillets() {
        List<Billet> liste = billetServices.listeBillets();
        // Un utilisateur ne reçoit que ses propres données ; un admin reçoit tout
        if (!session.estAdmin()) {
            liste = liste.stream()
                    .filter(element -> session.peutAcceder(proprietaire(element)))
                    .toList();
        }
        if (liste.isEmpty()) {
            return ResponseEntity.status(404).body("Aucun billet trouvé");
        }
        return ResponseEntity.ok(liste);
    }

    @GetMapping("/billet/{id}")
    public ResponseEntity<?> getBilletParId(@PathVariable Long id) {
        Billet billet = billetServices.rechercherBillet(id);
        if (billet == null) {
            return ResponseEntity.status(404).body("Billet non trouvé");
        }
        if (!session.peutAcceder(proprietaire(billet))) {
            return ResponseEntity.status(403).body("Accès refusé");
        }
        return ResponseEntity.ok(billet);
    }
}
