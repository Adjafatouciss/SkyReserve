package com.example.reservation_billets1.Controlleur;

import com.example.reservation_billets1.Modele.Admin;
import com.example.reservation_billets1.Services.AdminServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservationBillets")
@CrossOrigin(origins = "*")
public class AdminControlleur {

    @Autowired
    private AdminServices A_Services;

    @PostMapping("/ajouterAdmin")
    public ResponseEntity<?> ajouterAdmin(@Valid @RequestBody Admin A, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        Admin adminSaved = A_Services.ajouterAdmin(A);
        return ResponseEntity.status(201).body(adminSaved);
    }

    @DeleteMapping("/supprimerAdmin/{id}")
    public ResponseEntity<?> supprimerAdmin(@PathVariable Long id) {
        if (!A_Services.existeAdmin(id)) {
            return ResponseEntity.status(404).body("Admin non trouvé");
        }

        A_Services.supprimerAdmin(id);
        return ResponseEntity.ok("Admin supprimé avec succès");
    }

    @PutMapping("/modifierAdmin/{id}")
    public ResponseEntity<?> modifierAdmin(@PathVariable Long id,
                                           @Valid @RequestBody Admin A,
                                           BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        Admin adminUpdated = A_Services.modifierAdmin(id, A);

        if (adminUpdated == null) {
            return ResponseEntity.status(404).body("Admin non trouvé");
        }

        return ResponseEntity.ok(adminUpdated);
    }

    @GetMapping("/afficherTousLesAdmins")
    public ResponseEntity<?> afficherTousLesAdmins() {
        List<Admin> listeAdmins = A_Services.listeAdmins();

        if (listeAdmins.isEmpty()) {
            return ResponseEntity.status(404).body("Aucun admin trouvé");
        }

        return ResponseEntity.ok(listeAdmins);
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getAdminParId(@PathVariable Long id) {
        Admin admin = A_Services.rechercherAdmin(id);

        if (admin == null) {
            return ResponseEntity.status(404).body("Admin non trouvé");
        }

        return ResponseEntity.ok(admin);
    }
    @PostMapping("/loginAdmin")
    public ResponseEntity<?> loginAdmin(@RequestBody Admin A) {

        Admin admin = A_Services.login(A.getEmail(), A.getMotDePasse());

        if (admin == null) {
            return ResponseEntity.status(401).body("Email ou mot de passe incorrect");
        }

        return ResponseEntity.ok(admin);
    }
}