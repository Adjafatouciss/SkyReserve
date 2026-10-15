package com.example.reservation_billets1.Controlleur;

import com.example.reservation_billets1.Modele.Avion;
import com.example.reservation_billets1.Services.AvionServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservationBillets")
@CrossOrigin(origins = "*")
public class AvionControlleur {

    @Autowired
    private AvionServices avionServices;

    @PostMapping("/ajouterAvion")
    public ResponseEntity<?> ajouterAvion(@Valid @RequestBody Avion avion, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try {
            return ResponseEntity.status(201).body(avionServices.ajouterAvion(avion));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/supprimerAvion/{id}")
    public ResponseEntity<?> supprimerAvion(@PathVariable Long id) {
        if (!avionServices.existeAvion(id)) {
            return ResponseEntity.status(404).body("Avion non trouvé");
        }
        avionServices.supprimerAvion(id);
        return ResponseEntity.ok("Avion supprimé avec succès");
    }

    @PutMapping("/modifierAvion/{id}")
    public ResponseEntity<?> modifierAvion(@PathVariable Long id,
                                           @Valid @RequestBody Avion avion,
                                           BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try {
            Avion updated = avionServices.modifierAvion(id, avion);
            if (updated == null) {
                return ResponseEntity.status(404).body("Avion non trouvé");
            }
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/afficherTousLesAvions")
    public ResponseEntity<?> afficherTousLesAvions() {
        List<Avion> liste = avionServices.listeAvions();
        if (liste.isEmpty()) {
            return ResponseEntity.status(404).body("Aucun avion trouvé");
        }
        return ResponseEntity.ok(liste);
    }

    @GetMapping("/avion/{id}")
    public ResponseEntity<?> getAvionParId(@PathVariable Long id) {
        Avion avion = avionServices.rechercherAvion(id);
        if (avion == null) {
            return ResponseEntity.status(404).body("Avion non trouvé");
        }
        return ResponseEntity.ok(avion);
    }
}
