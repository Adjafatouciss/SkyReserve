package com.example.reservation_billets1.Repository;

import com.example.reservation_billets1.Modele.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByEmail(String email);
}
