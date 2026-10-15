package com.example.reservation_billets1.Repository;

import com.example.reservation_billets1.Modele.Billet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BilletRepository extends JpaRepository<Billet, Long> {
}
