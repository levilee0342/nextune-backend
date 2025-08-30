package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.Follow;
import com.example.nextune_backend.entity.FollowId;
import com.example.nextune_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    List<Follow> findAllByFollowerUser(User follower);
    List<Follow> findAllByFollowingUser(User following);
    boolean existsById(FollowId id);
}
