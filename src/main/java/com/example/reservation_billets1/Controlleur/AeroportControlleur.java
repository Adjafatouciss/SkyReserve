package com.example.reservation_billets1.Controlleur;

import com.example.reservation_billets1.Modele.Aeroport;
import com.example.reservation_billets1.Services.AeroportServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservationBillets")
@CrossOrigin(origins = "*")
public class AeroportControlleur {

    @Autowired
    private AeroportServices aeroportServices;

    @PostMapping("/ajouterAeroport")
    public ResponseEntity<?> ajouterAeroport(@Valid @RequestBody Aeroport aeroport, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        return ResponseEntity.status(201).body(aeroportServices.ajouterAeroport(aeroport));
    }

    @DeleteMapping("/supprimerAeroport/{id}")
    public ResponseEntity<?> supprimerAeroport(@PathVariable Long id) {
        if (!aeroportServices.existeAeroport(id)) {
            return ResponseEntity.status(404).body("Aéroport non trouvé");
        }
        aeroportServices.supprimerAeroport(id);
        return ResponseEntity.ok("Aéroport supprimé avec succès");
    }

    @PutMapping("/modifierAeroport/{id}")
    public ResponseEntity<?> modifierAeroport(@PathVariable Long id,
                                              @Valid @RequestBody Aeroport aeroport,
                                              BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        Aeroport updated = aeroportServices.modifierAeroport(id, aeroport);
        if (updated == null) {
            return ResponseEntity.status(404).body("Aéroport non trouvé");
        }
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/afficherTousLesAeroports")
    public ResponseEntity<?> afficherTousLesAeroports() {
        List<Aeroport> liste = aeroportServices.listeAeroports();
        if (liste.isEmpty()) {
            return ResponseEntity.status(404).body("Aucun aéroport trouvé");
        }
        return ResponseEntity.ok(liste);
    }

    @GetMapping("/aeroport/{id}")
    public ResponseEntity<?> getAeroportParId(@PathVariable Long id) {
        Aeroport aeroport = aeroportServices.rechercherAeroport(id);
        if (aeroport == null) {
            return ResponseEntity.status(404).body("Aéroport non trouvé");
        }
        return ResponseEntity.ok(aeroport);
    }
}
