package com.example.reservation_billets1.Controlleur;

import com.example.reservation_billets1.Modele.Vol;
import com.example.reservation_billets1.Services.VolServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/reservationBillets")
@CrossOrigin(origins = "*")
public class VolControlleur {

    @Autowired
    private VolServices volServices;

    @PostMapping("/ajouterVol")
    public ResponseEntity<?> ajouterVol(@Valid @RequestBody Vol vol, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try {
            return ResponseEntity.status(201).body(volServices.ajouterVol(vol));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/supprimerVol/{id}")
    public ResponseEntity<?> supprimerVol(@PathVariable Long id) {
        if (!volServices.existeVol(id)) {
            return ResponseEntity.status(404).body("Vol non trouvé");
        }
        volServices.supprimerVol(id);
        return ResponseEntity.ok("Vol supprimé avec succès");
    }

    @PutMapping("/modifierVol/{id}")
    public ResponseEntity<?> modifierVol(@PathVariable Long id,
                                         @Valid @RequestBody Vol vol,
                                         BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try {
            Vol updated = volServices.modifierVol(id, vol);
            if (updated == null) {
                return ResponseEntity.status(404).body("Vol non trouvé");
            }
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/afficherTousLesVols")
    public ResponseEntity<?> afficherTousLesVols() {
        List<Vol> listeVols = volServices.listeVols();
        if (listeVols.isEmpty()) {
            return ResponseEntity.status(404).body("Aucun vol trouvé");
        }
        return ResponseEntity.ok(listeVols);
    }

    @GetMapping("/vol/{id}")
    public ResponseEntity<?> getVolParId(@PathVariable Long id) {
        Vol vol = volServices.rechercherVol(id);
        if (vol == null) {
            return ResponseEntity.status(404).body("Vol non trouvé");
        }
        return ResponseEntity.ok(vol);
    }

    @GetMapping("/vols")
    public ResponseEntity<?> getAllVols() {
        List<Vol> vols = volServices.listeVols();
        return ResponseEntity.ok(vols);
    }
}
