package com.example.reservation_billets1.Controlleur;

import com.example.reservation_billets1.Modele.Billet;
import com.example.reservation_billets1.Services.BilletServices;
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

    @PostMapping("/ajouterBillet")
    public ResponseEntity<?> ajouterBillet(@Valid @RequestBody Billet billet, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
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
        return ResponseEntity.ok(billet);
    }
}
