package com.dangthuc.libman.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThanhVienForm {

    private Long id;

    @NotBlank(message = "username không được để trống")
    private String username;
    @NotBlank(message = "password không được để trống")
    private String password;
    private String hoTen;
    private int age;
    private String diaChi;
    private String email;
    private String phone;
    private String role;
}
