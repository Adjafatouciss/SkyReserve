package com.example.reservation_billets1.Repository;

import com.example.reservation_billets1.Modele.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
}
