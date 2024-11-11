package com.dangthuc.libman.service;

import com.dangthuc.libman.dto.request.ThanhVienForm;
import com.dangthuc.libman.entity.ThanhVien;
import com.dangthuc.libman.repository.ThanhVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThanhVienService {

    @Autowired
    private ThanhVienRepository thanhVienRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ThanhVien saveThanhVien(ThanhVien thanhVien) {
        if (thanhVien.getId() != null && thanhVienRepository.existsById(thanhVien.getId())) {
            // Đối tượng đã tồn tại, nên cập nhật
            ThanhVien existingThanhVien = thanhVienRepository.findById(thanhVien.getId()).orElseThrow();

            // Cập nhật thông tin mới
            existingThanhVien.setHoTen(thanhVien.getHoTen());
            existingThanhVien.setAge(thanhVien.getAge());
            existingThanhVien.setDiaChi(thanhVien.getDiaChi());
            existingThanhVien.setPhone(thanhVien.getPhone());

            return thanhVienRepository.save(existingThanhVien);
        } else {
            // Đối tượng chưa tồn tại, tiến hành tạo mới
            thanhVien.setPassword(this.passwordEncoder.encode(thanhVien.getPassword()));
            return thanhVienRepository.save(thanhVien);
        }
    }

    public ThanhVien getThanhVienById(Long id) {
        return thanhVienRepository.findById(id).get();
    }

    public void deleteThanhVien(Long id) {
        thanhVienRepository.deleteById(id);
    }

    public List<ThanhVien> getAllThanhVien() {
        return thanhVienRepository.findAll();
    }

    public ThanhVien getThanhVienByUsername(String username) {
        return thanhVienRepository.findByUsername(username);
    }
}
