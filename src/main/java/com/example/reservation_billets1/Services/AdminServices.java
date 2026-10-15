package com.example.reservation_billets1.Services;

import com.example.reservation_billets1.Modele.Admin;
import com.example.reservation_billets1.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Indique que c’est une classe métier (logique)
public class AdminServices {

    @Autowired
    private AdminRepository adminRepository;

    // Ajouter un admin
    public Admin ajouterAdmin(Admin admin) {
        admin.setRole(Admin.ROLE_ADMIN);
        return adminRepository.save(admin);
    }

    // Vérifier si admin existe
    public boolean existeAdmin(Long id) {
        return adminRepository.existsById(id);
    }

    // Supprimer admin
    public void supprimerAdmin(Long id) {
        adminRepository.deleteById(id);
    }

    // Modifier admin
    public Admin modifierAdmin(Long id, Admin admin) {

        Admin adminExistant = adminRepository.findById(id).orElse(null);

        if (adminExistant == null) {
            return null;
        }

        adminExistant.setNom(admin.getNom());
        adminExistant.setPrenom(admin.getPrenom());
        adminExistant.setEmail(admin.getEmail());
        adminExistant.setMotDePasse(admin.getMotDePasse());
        adminExistant.setRole(Admin.ROLE_ADMIN);

        return adminRepository.save(adminExistant);
    }

    // Afficher tous les admins
    public List<Admin> listeAdmins() {
        return adminRepository.findAll();
    }

    // Rechercher admin par ID
    public Admin rechercherAdmin(Long id) {
        return adminRepository.findById(id).orElse(null);
    }


    // Methode pour de connecter
    public Admin login(String email, String motDePasse) {

        Admin admin = adminRepository.findByEmail(email);

        if (admin == null || !admin.getMotDePasse().equals(motDePasse)) {
            return null;
        }

        if (admin.getRole() == null || admin.getRole().isBlank()) {
            admin.setRole(Admin.ROLE_ADMIN);
            admin = adminRepository.save(admin);
        }

        if (!Admin.ROLE_ADMIN.equalsIgnoreCase(admin.getRole())) {
            return null;
        }

        return admin;
    }

}