package com.example.nextune_backend.security;


import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.repository.RoleRepository;
import com.example.nextune_backend.repository.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserPrincipal implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Email " + email + " was not found in database!"
                ));

        String roleName = roleRepository.findRoleNameByUserId(user.getId());
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));
        String password = user.getPassword();
        if (password == null || password.isBlank()) {
            password = "{noop}oauth2";
        }
        System.out.println(user.getEmail() +" "+password +" "+ roleName + " "+ authorities);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                password,
                authorities
        );
    }


}
