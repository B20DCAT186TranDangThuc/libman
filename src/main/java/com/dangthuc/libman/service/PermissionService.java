package com.dangthuc.libman.service;

import com.dangthuc.libman.entity.Permission;
import com.dangthuc.libman.repository.PermissionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    public boolean isPermissionExist(@Valid Permission permission) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod()
        );
    }

    public Permission fetchById(long id) {
        Optional<Permission> permission = permissionRepository.findById(id);
        return permission.orElse(null);
    }

    public Permission updatePermission(@Valid Permission permission) {
        Permission p = fetchById(permission.getId());
        p.setApiPath(permission.getApiPath());
        p.setMethod(permission.getMethod());
        p.setModule(permission.getModule());

        return permissionRepository.save(p);
    }
}
