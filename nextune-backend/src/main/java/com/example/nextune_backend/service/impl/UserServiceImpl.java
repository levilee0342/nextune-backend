package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.GoogleUserProfile;
import com.example.nextune_backend.entity.Role;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.entity.enums.AuthProvider;
import com.example.nextune_backend.entity.enums.RoleName;
import com.example.nextune_backend.entity.enums.Status;
import com.example.nextune_backend.repository.RoleRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private static final RoleName DEFAULT_ROLE = RoleName.USER;

    @Override
    public User upsertGoogleUser(GoogleUserProfile g) {
        // Phòng null (tuỳ case Google trả về)
        String sub     = Optional.ofNullable(g.getSub()).orElseThrow(() -> new IllegalArgumentException("Google sub is null"));
        String email   = g.getEmail();   // có thể null nếu user ẩn email
        String name    = g.getName();
        String picture = g.getPicture();

        // 1) Tìm theo googleSub
        User user = userRepository.findByGoogleSub(sub).orElse(null);
        if (user != null) {
            boolean dirty = false;
            if (email != null && !email.equals(user.getEmail())) { user.setEmail(email); dirty = true; }
            if (name  != null && !name.equals(user.getName()))   { user.setName(name); dirty = true; }
            if (picture!= null && !picture.equals(user.getAvatar())) { user.setAvatar(picture); dirty = true; }
            if (user.getProvider() != AuthProvider.GOOGLE) { user.setProvider(AuthProvider.GOOGLE); dirty = true; }
            if (user.getRole() == null) { user.setRole(getDefaultRole()); dirty = true; }
            return dirty ? userRepository.save(user) : user;
        }

        // 2) Chưa link sub nhưng đã có user theo email -> link
        if (email != null) {
            user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                user.setGoogleSub(sub);
                if (user.getName() == null && name != null)   user.setName(name);
                if (user.getAvatar() == null && picture != null) user.setAvatar(picture);
                user.setProvider(AuthProvider.GOOGLE);
                if (user.getRole() == null) user.setRole(getDefaultRole());
                return userRepository.save(user);
            }
        }

        // 3) Tạo mới
        User created = new User();
        created.setEmail(email);
        created.setName(name);
        created.setAvatar(picture);
        created.setGoogleSub(sub);
        created.setProvider(AuthProvider.GOOGLE);
        created.setRole(getDefaultRole());
        created.setStatus(Status.ACTIVE);
        created.setCreatedAt(LocalDateTime.now());

        return userRepository.save(created);
    }

    private Role getDefaultRole() {
        return roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new IllegalStateException("Missing role: " + DEFAULT_ROLE));
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(String id) {
        // Kiểm tra UUID “chuẩn” thay vì length
        try { UUID.fromString(id); }
        catch (Exception e) { throw new IllegalArgumentException("User id is not a valid UUID: " + id); }

        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }
}
