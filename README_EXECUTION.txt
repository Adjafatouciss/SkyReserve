PROJET RÉSERVATION BILLETS — GUIDE RAPIDE

1) Base de données
- Crée une base MySQL nommée : r_Billets
- Vérifie dans src/main/resources/application.properties :
  - url = jdbc:mysql://localhost:3306/r_Billets
  - username = root
  - password =

2) Lancement
- Démarre MySQL
- Lance le projet Spring Boot
- Le serveur est configuré sur : http://localhost:8080

3) Frontend
- Ouvre directement :
  http://localhost:8080/login.html

4) Connexion admin
- Il faut d'abord avoir un admin en base
- Exemple de JSON à envoyer avec Postman si besoin :
  POST http://localhost:8080/api/reservationBillets/ajouterAdmin
  {
    "nom": "Admin",
    "prenom": "Principal",
    "email": "admin@email.com",
    "motDePasse": "1234"
  }

5) Ordre conseillé de création des données
- Admins
- Utilisateurs
- Compagnies
- Avions
- Aéroports
- Vols
- Réservations
- Billets
- Paiements
