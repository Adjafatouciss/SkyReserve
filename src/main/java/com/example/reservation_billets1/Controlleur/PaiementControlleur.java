package com.example.reservation_billets1.Controlleur;

import com.example.reservation_billets1.Modele.Paiement;
import com.example.reservation_billets1.Services.PaiementServices;
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

    @PostMapping("/ajouterPaiement")
    public ResponseEntity<?> ajouterPaiement(@Valid @RequestBody Paiement paiement, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
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
        return ResponseEntity.ok(paiement);
    }
}
