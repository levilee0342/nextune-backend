package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.Role;
import com.example.nextune_backend.entity.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(RoleName name);
    @Query("SELECT u.role.name FROM User u WHERE u.id = :userId")
    String findRoleNameByUserId(@Param("userId") String userId);

}

