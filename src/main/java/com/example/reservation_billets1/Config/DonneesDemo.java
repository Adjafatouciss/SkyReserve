package com.example.reservation_billets1.Config;

import com.example.reservation_billets1.Modele.*;
import com.example.reservation_billets1.Repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Crée des données de démonstration au démarrage, pour que les visiteurs
 * du portfolio puissent tester l'application tout de suite :
 * compagnies, avions, aéroports, vols, un compte voyageur et un compte admin.
 *
 * Activé seulement si la variable d'environnement DEMO_DATA vaut true,
 * et seulement si la base ne contient encore aucun vol.
 */
@Component
@Order(2) // après MigrationMotsDePasse
public class DonneesDemo implements CommandLineRunner {

    public static final String EMAIL_VOYAGEUR = "demo@skyreserve.ca";
    public static final String EMAIL_ADMIN = "admin@skyreserve.ca";
    public static final String MOT_DE_PASSE_DEMO = "demo1234";

    private final boolean active;
    private final CompagnieAerienneRepository compagnies;
    private final AvionRepository avions;
    private final AeroportRepository aeroports;
    private final VolRepository vols;
    private final UtilisateurRepository utilisateurs;
    private final AdminRepository admins;
    private final PasswordEncoder passwordEncoder;

    public DonneesDemo(@Value("${app.demo-data:false}") boolean active,
                       CompagnieAerienneRepository compagnies,
                       AvionRepository avions,
                       AeroportRepository aeroports,
                       VolRepository vols,
                       UtilisateurRepository utilisateurs,
                       AdminRepository admins,
                       PasswordEncoder passwordEncoder) {
        this.active = active;
        this.compagnies = compagnies;
        this.avions = avions;
        this.aeroports = aeroports;
        this.vols = vols;
        this.utilisateurs = utilisateurs;
        this.admins = admins;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!active) {
            return;
        }

        creerComptesDemo();

        // Les vols ne sont créés qu'une seule fois (base vide)
        if (vols.count() > 0) {
            return;
        }

        // Compagnies
        CompagnieAerienne airCanada = compagnie("Air Canada", "Canada", "AC");
        CompagnieAerienne airFrance = compagnie("Air France", "France", "AF");
        CompagnieAerienne airSenegal = compagnie("Air Sénégal", "Sénégal", "HC");

        // Avions
        Avion a330 = avion("Airbus A330", 297, "C-GDEM", airCanada);
        Avion b777 = avion("Boeing 777", 380, "F-GDEM", airFrance);
        Avion a321 = avion("Airbus A321neo", 190, "6V-DEM", airSenegal);

        // Aéroports
        Aeroport montreal = aeroport("Montréal-Trudeau", "Montréal", "Canada", "YUL");
        Aeroport paris = aeroport("Paris-Charles-de-Gaulle", "Paris", "France", "CDG");
        Aeroport dakar = aeroport("Blaise-Diagne", "Dakar", "Sénégal", "DSS");
        Aeroport toronto = aeroport("Toronto-Pearson", "Toronto", "Canada", "YYZ");

        // Vols : dates calculées à partir d'aujourd'hui pour rester toujours à venir
        LocalDate j = LocalDate.now();
        vol("AC870", j.plusDays(14), "19:30", j.plusDays(15), "08:35", 689.0, airCanada, a330, montreal, paris);
        vol("AF345", j.plusDays(16), "11:20", j.plusDays(16), "13:10", 712.0, airFrance, b777, paris, montreal);
        vol("HC401", j.plusDays(21), "22:45", j.plusDays(22), "08:05", 845.0, airSenegal, a321, montreal, dakar);
        vol("HC402", j.plusDays(30), "10:15", j.plusDays(30), "15:40", 829.0, airSenegal, a321, dakar, montreal);
        vol("AC402", j.plusDays(7), "07:00", j.plusDays(7), "08:20", 189.0, airCanada, a330, montreal, toronto);
        vol("AC405", j.plusDays(9), "18:00", j.plusDays(9), "19:20", 175.0, airCanada, a330, toronto, montreal);
        vol("AF718", j.plusDays(25), "16:05", j.plusDays(25), "21:30", 399.0, airFrance, b777, paris, dakar);
    }

    private void creerComptesDemo() {
        boolean voyageurExiste = utilisateurs.findAll().stream()
                .anyMatch(u -> EMAIL_VOYAGEUR.equalsIgnoreCase(u.getEmail()));
        if (!voyageurExiste) {
            Utilisateur u = new Utilisateur();
            u.setNom("Demo");
            u.setPrenom("Visiteur");
            u.setEmail(EMAIL_VOYAGEUR);
            u.setMotDePasse(passwordEncoder.encode(MOT_DE_PASSE_DEMO));
            u.setTelephone("514-555-0100");
            utilisateurs.save(u);
        }

        if (admins.findByEmail(EMAIL_ADMIN) == null) {
            Admin a = new Admin();
            a.setNom("Demo");
            a.setPrenom("Admin");
            a.setEmail(EMAIL_ADMIN);
            a.setMotDePasse(passwordEncoder.encode(MOT_DE_PASSE_DEMO));
            admins.save(a);
        }
    }

    private CompagnieAerienne compagnie(String nom, String pays, String code) {
        CompagnieAerienne c = new CompagnieAerienne();
        c.setNomCompagnie(nom);
        c.setPays(pays);
        c.setCodeCompagnie(code);
        return compagnies.save(c);
    }

    private Avion avion(String modele, int capacite, String immatriculation, CompagnieAerienne compagnie) {
        Avion a = new Avion();
        a.setModele(modele);
        a.setCapacite(capacite);
        a.setImmatriculation(immatriculation);
        a.setCompagnieAerienne(compagnie);
        return avions.save(a);
    }

    private Aeroport aeroport(String nom, String ville, String pays, String code) {
        Aeroport a = new Aeroport();
        a.setNomAeroport(nom);
        a.setVille(ville);
        a.setPays(pays);
        a.setCodeAeroport(code);
        return aeroports.save(a);
    }

    private void vol(String numero, LocalDate dateDepart, String heureDepart,
                     LocalDate dateArrivee, String heureArrivee, double prix,
                     CompagnieAerienne compagnie, Avion avion, Aeroport depart, Aeroport arrivee) {
        Vol v = new Vol();
        v.setNumeroVol(numero);
        v.setDateDepart(dateDepart.toString());
        v.setHeureDepart(heureDepart);
        v.setDateArrivee(dateArrivee.toString());
        v.setHeureArrivee(heureArrivee);
        v.setPrix(prix);
        v.setCompagnieAerienne(compagnie);
        v.setAvion(avion);
        v.setAeroportDepart(depart);
        v.setAeroportArrivee(arrivee);
        vols.save(v);
    }
}
