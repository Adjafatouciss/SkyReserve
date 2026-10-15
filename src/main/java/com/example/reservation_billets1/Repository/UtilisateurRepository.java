package com.example.reservation_billets1.Repository;

import com.example.reservation_billets1.Modele.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Utilisateur findByEmailAndMotDePasse(String email, String motDePasse);

}
