package com.dangthuc.libman.service;

import com.dangthuc.libman.entity.ThanhVien;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class UserDetailsCustom implements UserDetailsService {

    @Autowired
    private ThanhVienService thanhVienService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ThanhVien thanhVien = this.thanhVienService.getThanhVienByUsername(username);
        return new User(
                thanhVien.getUsername(),
                thanhVien.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + thanhVien.getRole()))
        );
    }
}
