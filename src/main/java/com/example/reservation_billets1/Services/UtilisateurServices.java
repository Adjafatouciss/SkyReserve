package com.example.reservation_billets1.Services;
import com.example.reservation_billets1.Modele.Utilisateur;
import com.example.reservation_billets1.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurServices {

    // Instantier la classe UtilisateurRepository pour l'utiliser
    @Autowired
    private UtilisateurRepository U_Repository;

    // Methode pour ajouter un utilisateur
    public Utilisateur AjouterUtilisateur(Utilisateur U){
         U.setRole(Utilisateur.ROLE_UTILISATEUR);
         return U_Repository.save(U);
    }

    // <Methode pour supprimer un utilisateur

    // On verifie si l'id existe

    public boolean existeUtilisateur(Long id) {

        return U_Repository.existsById(id);
    }
    public void SupprimerUtilisasteur(Long   id){
        U_Repository.deleteById(id);
    }

    // Methode pour modifier un utilisateur
    public Utilisateur modifierUtilisateur(Long id,Utilisateur U)
    {
        // trouver l'utilisateur a modifier
        Utilisateur User= U_Repository.findById(id).orElse(null);

        if(User == null){
            return  null;
        }

        // Modifier les champs
        User.setNom(U.getNom());
        User.setPrenom(U.getPrenom());
        User.setEmail(U.getEmail());
        User.setMotDePasse(U.getMotDePasse());
        User.setTelephone(U.getTelephone());
        User.setRole(Utilisateur.ROLE_UTILISATEUR);

        // Sauvegarder la modification dans la base
        return U_Repository.save(User);
    }

    // Afficher tous les utilisateurs
    public List<Utilisateur> ListeDesUtilisateurs(){
        return U_Repository.findAll();
    }

    // Rechercher un utilisateur par son id

    public Utilisateur RechercherUtilisateur(Long id){

        return U_Repository.findById(id).orElse(null);
    }
    public Utilisateur login(String email, String motDePasse) {
        Utilisateur user = U_Repository.findByEmailAndMotDePasse(email, motDePasse);

        if (user == null) {
            return null;
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole(Utilisateur.ROLE_UTILISATEUR);
            user = U_Repository.save(user);
        }

        if (!Utilisateur.ROLE_UTILISATEUR.equalsIgnoreCase(user.getRole())) {
            return null;
        }

        return user;
    }

}
