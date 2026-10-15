package com.example.reservation_billets1.Services;

import com.example.reservation_billets1.Modele.Aeroport;
import com.example.reservation_billets1.Modele.Avion;
import com.example.reservation_billets1.Modele.CompagnieAerienne;
import com.example.reservation_billets1.Modele.Vol;
import com.example.reservation_billets1.Repository.AeroportRepository;
import com.example.reservation_billets1.Repository.AvionRepository;
import com.example.reservation_billets1.Repository.CompagnieAerienneRepository;
import com.example.reservation_billets1.Repository.VolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolServices {

    @Autowired
    private VolRepository volRepository;

    @Autowired
    private CompagnieAerienneRepository compagnieRepository;

    @Autowired
    private AvionRepository avionRepository;

    @Autowired
    private AeroportRepository aeroportRepository;

    public Vol ajouterVol(Vol vol) {
        hydraterRelations(vol);
        return volRepository.save(vol);
    }

    public boolean existeVol(Long id) {
        return volRepository.existsById(id);
    }

    public void supprimerVol(Long id) {
        volRepository.deleteById(id);
    }

    public Vol modifierVol(Long id, Vol vol) {
        Vol existant = volRepository.findById(id).orElse(null);
        if (existant == null) {
            return null;
        }

        existant.setNumeroVol(vol.getNumeroVol());
        existant.setDateDepart(vol.getDateDepart());
        existant.setHeureDepart(vol.getHeureDepart());
        existant.setDateArrivee(vol.getDateArrivee());
        existant.setHeureArrivee(vol.getHeureArrivee());
        existant.setPrix(vol.getPrix());

        vol.setId(id);
        hydraterRelations(vol);

        existant.setCompagnieAerienne(vol.getCompagnieAerienne());
        existant.setAvion(vol.getAvion());
        existant.setAeroportDepart(vol.getAeroportDepart());
        existant.setAeroportArrivee(vol.getAeroportArrivee());
        return volRepository.save(existant);
    }

    public List<Vol> listeVols() {
        return volRepository.findAll();
    }

    public Vol rechercherVol(Long id) {
        return volRepository.findById(id).orElse(null);
    }

    private void hydraterRelations(Vol vol) {
        vol.setCompagnieAerienne(resoudreCompagnie(vol));
        vol.setAvion(resoudreAvion(vol));
        vol.setAeroportDepart(resoudreAeroportDepart(vol));
        vol.setAeroportArrivee(resoudreAeroportArrivee(vol));
    }

    private CompagnieAerienne resoudreCompagnie(Vol vol) {
        if (vol.getCompagnieAerienne() == null || vol.getCompagnieAerienne().getId() == null) {
            throw new IllegalArgumentException("La compagnie aérienne est obligatoire.");
        }
        return compagnieRepository.findById(vol.getCompagnieAerienne().getId())
                .orElseThrow(() -> new IllegalArgumentException("Compagnie aérienne introuvable."));
    }

    private Avion resoudreAvion(Vol vol) {
        if (vol.getAvion() == null || vol.getAvion().getId() == null) {
            throw new IllegalArgumentException("L'avion est obligatoire.");
        }
        return avionRepository.findById(vol.getAvion().getId())
                .orElseThrow(() -> new IllegalArgumentException("Avion introuvable."));
    }

    private Aeroport resoudreAeroportDepart(Vol vol) {
        if (vol.getAeroportDepart() == null || vol.getAeroportDepart().getId() == null) {
            throw new IllegalArgumentException("L'aéroport de départ est obligatoire.");
        }
        return aeroportRepository.findById(vol.getAeroportDepart().getId())
                .orElseThrow(() -> new IllegalArgumentException("Aéroport de départ introuvable."));
    }

    private Aeroport resoudreAeroportArrivee(Vol vol) {
        if (vol.getAeroportArrivee() == null || vol.getAeroportArrivee().getId() == null) {
            throw new IllegalArgumentException("L'aéroport d'arrivée est obligatoire.");
        }
        return aeroportRepository.findById(vol.getAeroportArrivee().getId())
                .orElseThrow(() -> new IllegalArgumentException("Aéroport d'arrivée introuvable."));
    }
}
