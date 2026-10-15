package com.example.reservation_billets1.Controlleur;

import com.example.reservation_billets1.Modele.Utilisateur;
import com.example.reservation_billets1.Repository.UtilisateurRepository;
import com.example.reservation_billets1.Services.UtilisateurServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/reservationBillets")
@CrossOrigin(origins = "*")
public class UtilisateurControlleur {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Instantier la classe service pour pouvoir l'utiliser
    @Autowired
    private UtilisateurServices U_Services;

    // La methode controllerur avant ajouter
    @PostMapping("/ajouterUtilisateur")
    public ResponseEntity<?> AjouterUtilisateur(@Valid @RequestBody Utilisateur U, BindingResult result ){
        if(result.hasErrors()){
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        // Pas d'erreur ajouter l'utilisateur
        Utilisateur userSaved= U_Services.AjouterUtilisateur(U);
        return  ResponseEntity.status(201).body(userSaved);
    }


    // La methode supprimer avec id
    @DeleteMapping("/supprimerUtilisateur/{id}")
    public ResponseEntity<?> supprimerUtilisateur(@PathVariable Long id){

        if(!U_Services.existeUtilisateur(id)){
            return ResponseEntity.status(404).body("Utilisateur non trouvé");
        }

        U_Services.SupprimerUtilisasteur(id);
        return ResponseEntity.ok("Utilisateur supprimé avec succès");
    }


    // Methode pour modifier un utilisateur
    @PutMapping("/modifierUtilisateur/{id}")
    public ResponseEntity<?> modifierUtilisateur(@PathVariable Long id,
                                                 @Valid @RequestBody Utilisateur U,
                                                 BindingResult result){

        // Vérification des champs
        if(result.hasErrors()){
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        // Appeler le service
        Utilisateur userUpdated = U_Services.modifierUtilisateur(id, U);

        // Vérifier si existe
        if(userUpdated == null){
            return ResponseEntity.status(404).body("Utilisateur non trouvé");
        }

        return ResponseEntity.ok(userUpdated);
    }

    // Afficher tous les utulisateurs

    @GetMapping("/afficherTousLesUtilisateurs")
    public ResponseEntity<?> afficherTousLesUtilisateurs(){

        List<Utilisateur> listeUtilisateurs = U_Services.ListeDesUtilisateurs();

        if(listeUtilisateurs.isEmpty()){
            return ResponseEntity.status(404).body("Aucun utilisateur trouvé");
        }

        return ResponseEntity.ok(listeUtilisateurs);
    }

    //  Rechercher un utilisateur par son id
    @GetMapping("/utilisateur/{id}")
    public ResponseEntity<?> getUtilisateurParId(@PathVariable Long id){

        Utilisateur user = U_Services.RechercherUtilisateur(id);

        if(user == null){
            return ResponseEntity.status(404).body("Utilisateur non trouvé");
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/loginUtilisateur")
    public ResponseEntity<?> loginUtilisateur(@RequestBody Utilisateur u) {

        Utilisateur user = U_Services.login(u.getEmail(), u.getMotDePasse());

        if (user == null) {
            return ResponseEntity.status(401).body("Email ou mot de passe incorrect");
        }

        return ResponseEntity.ok(user);
    }


}
