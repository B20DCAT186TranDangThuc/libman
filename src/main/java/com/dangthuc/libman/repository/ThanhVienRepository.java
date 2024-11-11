package com.dangthuc.libman.repository;

import com.dangthuc.libman.entity.ThanhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThanhVienRepository extends JpaRepository<ThanhVien, Long> {

    ThanhVien findByUsername(String username);
}
