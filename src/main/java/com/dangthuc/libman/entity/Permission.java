package com.dangthuc.libman.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tblPermissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "name không được để trống")
    private String name;
    @NotBlank(message = "apiPath không được để trống")
    private String apiPath;
    @NotBlank(message = "method không được để trống")
    private String method;
    @NotBlank(message = "module không được để trống")
    private String module;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    @JsonIgnore
    private List<Role> roles;
}