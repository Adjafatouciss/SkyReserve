package com.example.reservation_billets1.Repository;

import com.example.reservation_billets1.Modele.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUtilisateurId(Long utilisateurId);
}
