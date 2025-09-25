package com.example.nextune_backend.utility;

import com.example.nextune_backend.dto.GoogleUserProfile;
import com.example.nextune_backend.entity.enums.RoleName;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.repository.UserRepository;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Optional;

@Component
public class UserUtility {
    @Autowired
    UserRepository userRepository;
    public String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Unauthenticated");
        }
        Optional<User> user = userRepository.findByEmail(auth.getName());
        System.out.println(user.get().getId());
        return user.get().getId();
    }

    public RoleName getCurrentUserRoleName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Unauthenticated");
        }
        Optional<User> user = userRepository.findByEmail(auth.getName());
        System.out.println(user.get().getRole().getName());
        return user.get().getRole().getName();
    }
    GoogleUserProfile toProfile(JWTClaimsSet c) {
        try {
            return new GoogleUserProfile(
                    c.getStringClaim("sub"),
                    c.getStringClaim("email"),
                    (Boolean) c.getClaim("email_verified"),
                    c.getStringClaim("name"),
                    c.getStringClaim("picture")
            );
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse Google user profile claims", e);
        }
    }
}
