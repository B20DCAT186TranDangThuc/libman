package com.dangthuc.libman.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tblThanhVien")
public class ThanhVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    @Column(name = "age")
    private int age;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "phone")
    private String phone;

}
