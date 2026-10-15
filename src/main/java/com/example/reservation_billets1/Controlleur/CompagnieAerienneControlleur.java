package com.example.reservation_billets1.Controlleur;

import com.example.reservation_billets1.Modele.CompagnieAerienne;
import com.example.reservation_billets1.Services.CompagnieAerienneServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservationBillets")
@CrossOrigin(origins = "*")
public class CompagnieAerienneControlleur {

    @Autowired
    private CompagnieAerienneServices compagnieServices;

    @PostMapping("/ajouterCompagnie")
    public ResponseEntity<?> ajouterCompagnie(@Valid @RequestBody CompagnieAerienne compagnie, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        return ResponseEntity.status(201).body(compagnieServices.ajouterCompagnie(compagnie));
    }

    @DeleteMapping("/supprimerCompagnie/{id}")
    public ResponseEntity<?> supprimerCompagnie(@PathVariable Long id) {
        if (!compagnieServices.existeCompagnie(id)) {
            return ResponseEntity.status(404).body("Compagnie non trouvée");
        }
        compagnieServices.supprimerCompagnie(id);
        return ResponseEntity.ok("Compagnie supprimée avec succès");
    }

    @PutMapping("/modifierCompagnie/{id}")
    public ResponseEntity<?> modifierCompagnie(@PathVariable Long id,
                                               @Valid @RequestBody CompagnieAerienne compagnie,
                                               BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        CompagnieAerienne updated = compagnieServices.modifierCompagnie(id, compagnie);
        if (updated == null) {
            return ResponseEntity.status(404).body("Compagnie non trouvée");
        }
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/afficherToutesLesCompagnies")
    public ResponseEntity<?> afficherToutesLesCompagnies() {
        List<CompagnieAerienne> liste = compagnieServices.listeCompagnies();
        if (liste.isEmpty()) {
            return ResponseEntity.status(404).body("Aucune compagnie trouvée");
        }
        return ResponseEntity.ok(liste);
    }

    @GetMapping("/compagnie/{id}")
    public ResponseEntity<?> getCompagnieParId(@PathVariable Long id) {
        CompagnieAerienne compagnie = compagnieServices.rechercherCompagnie(id);
        if (compagnie == null) {
            return ResponseEntity.status(404).body("Compagnie non trouvée");
        }
        return ResponseEntity.ok(compagnie);
    }
}
