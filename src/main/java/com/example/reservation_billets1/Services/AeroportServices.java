package com.example.reservation_billets1.Services;

import com.example.reservation_billets1.Modele.Aeroport;
import com.example.reservation_billets1.Repository.AeroportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AeroportServices {

    @Autowired
    private AeroportRepository aeroportRepository;

    public Aeroport ajouterAeroport(Aeroport aeroport) {
        return aeroportRepository.save(aeroport);
    }

    public boolean existeAeroport(Long id) {
        return aeroportRepository.existsById(id);
    }

    public void supprimerAeroport(Long id) {
        aeroportRepository.deleteById(id);
    }

    public Aeroport modifierAeroport(Long id, Aeroport aeroport) {
        Aeroport existant = aeroportRepository.findById(id).orElse(null);
        if (existant == null) {
            return null;
        }

        existant.setNomAeroport(aeroport.getNomAeroport());
        existant.setVille(aeroport.getVille());
        existant.setPays(aeroport.getPays());
        existant.setCodeAeroport(aeroport.getCodeAeroport());
        return aeroportRepository.save(existant);
    }

    public List<Aeroport> listeAeroports() {
        return aeroportRepository.findAll();
    }

    public Aeroport rechercherAeroport(Long id) {
        return aeroportRepository.findById(id).orElse(null);
    }
}
