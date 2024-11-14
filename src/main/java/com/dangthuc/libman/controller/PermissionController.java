package com.dangthuc.libman.controller;

import com.dangthuc.libman.entity.Permission;
import com.dangthuc.libman.exception.IdInvalidException;
import com.dangthuc.libman.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping()
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        if (this.permissionService.isPermissionExist(permission)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.createPermission(permission));
    }

    @PutMapping()
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        if (this.permissionService.fetchById(permission.getId()) == null) {
            throw new IdInvalidException("permission với id = " + permission.getId() + "không tồn tại");
        }

        if (this.permissionService.isPermissionExist(permission)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }

        return ResponseEntity.ok().body(this.permissionService.updatePermission(permission));
    }
}


