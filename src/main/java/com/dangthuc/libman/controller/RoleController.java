package com.dangthuc.libman.controller;

import com.dangthuc.libman.service.RoleSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleSevice roleSevice;
}
