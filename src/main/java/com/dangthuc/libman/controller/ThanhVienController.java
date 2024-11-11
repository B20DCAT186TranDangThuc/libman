package com.dangthuc.libman.controller;

import com.dangthuc.libman.entity.ThanhVien;
import com.dangthuc.libman.exception.IdInvalidException;
import com.dangthuc.libman.service.ThanhVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thanh-vien")
public class ThanhVienController {

    @Autowired
    private ThanhVienService thanhVienService;

    @PostMapping()
    public ResponseEntity<ThanhVien> createThanhVien(@RequestBody ThanhVien thanhVien) {
        ThanhVien tv = this.thanhVienService.saveThanhVien(thanhVien);
        return ResponseEntity.status(HttpStatus.CREATED).body(tv);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteThanhVien(@PathVariable("id") Long id) {
        this.thanhVienService.deleteThanhVien(id);

        return ResponseEntity.status(HttpStatus.OK).body("delete success");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThanhVien> getThanhVien(@PathVariable("id") Long id) throws IdInvalidException {

        if (id > 1500) {
            throw new IdInvalidException("Id khong lon hon 1500");
        }
        ThanhVien fetchThanhVien = this.thanhVienService.getThanhVienById(id);

        return ResponseEntity.status(HttpStatus.OK).body(fetchThanhVien);
    }

    @GetMapping()
    public ResponseEntity<List<ThanhVien>> getAllThanhVien() {
        return ResponseEntity.ok(this.thanhVienService.getAllThanhVien());
    }

    @PutMapping()
    public ResponseEntity<ThanhVien> updateThanhVien(@RequestBody ThanhVien thanhVien) {
        return ResponseEntity.ok(this.thanhVienService.saveThanhVien(thanhVien));
    }
}
