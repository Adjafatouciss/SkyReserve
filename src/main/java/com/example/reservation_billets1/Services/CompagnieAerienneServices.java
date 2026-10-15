package com.example.reservation_billets1.Services;

import com.example.reservation_billets1.Modele.CompagnieAerienne;
import com.example.reservation_billets1.Repository.CompagnieAerienneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompagnieAerienneServices {

    @Autowired
    private CompagnieAerienneRepository compagnieRepository;

    public CompagnieAerienne ajouterCompagnie(CompagnieAerienne compagnie) {
        return compagnieRepository.save(compagnie);
    }

    public boolean existeCompagnie(Long id) {
        return compagnieRepository.existsById(id);
    }

    public void supprimerCompagnie(Long id) {
        compagnieRepository.deleteById(id);
    }

    public CompagnieAerienne modifierCompagnie(Long id, CompagnieAerienne compagnie) {
        CompagnieAerienne existante = compagnieRepository.findById(id).orElse(null);
        if (existante == null) {
            return null;
        }

        existante.setNomCompagnie(compagnie.getNomCompagnie());
        existante.setPays(compagnie.getPays());
        existante.setCodeCompagnie(compagnie.getCodeCompagnie());
        return compagnieRepository.save(existante);
    }

    public List<CompagnieAerienne> listeCompagnies() {
        return compagnieRepository.findAll();
    }

    public CompagnieAerienne rechercherCompagnie(Long id) {
        return compagnieRepository.findById(id).orElse(null);
    }
}
