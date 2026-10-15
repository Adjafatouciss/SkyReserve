package com.example.reservation_billets1.Services;

import com.example.reservation_billets1.Modele.Avion;
import com.example.reservation_billets1.Modele.CompagnieAerienne;
import com.example.reservation_billets1.Repository.AvionRepository;
import com.example.reservation_billets1.Repository.CompagnieAerienneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvionServices {

    @Autowired
    private AvionRepository avionRepository;

    @Autowired
    private CompagnieAerienneRepository compagnieRepository;

    public Avion ajouterAvion(Avion avion) {
        avion.setCompagnieAerienne(resoudreCompagnie(avion));
        return avionRepository.save(avion);
    }

    public boolean existeAvion(Long id) {
        return avionRepository.existsById(id);
    }

    public void supprimerAvion(Long id) {
        avionRepository.deleteById(id);
    }

    public Avion modifierAvion(Long id, Avion avion) {
        Avion existant = avionRepository.findById(id).orElse(null);
        if (existant == null) {
            return null;
        }

        existant.setModele(avion.getModele());
        existant.setCapacite(avion.getCapacite());
        existant.setImmatriculation(avion.getImmatriculation());
        existant.setCompagnieAerienne(resoudreCompagnie(avion));
        return avionRepository.save(existant);
    }

    public List<Avion> listeAvions() {
        return avionRepository.findAll();
    }

    public Avion rechercherAvion(Long id) {
        return avionRepository.findById(id).orElse(null);
    }

    private CompagnieAerienne resoudreCompagnie(Avion avion) {
        if (avion.getCompagnieAerienne() == null || avion.getCompagnieAerienne().getId() == null) {
            throw new IllegalArgumentException("La compagnie aérienne est obligatoire.");
        }

        return compagnieRepository.findById(avion.getCompagnieAerienne().getId())
                .orElseThrow(() -> new IllegalArgumentException("Compagnie aérienne introuvable."));
    }
}
