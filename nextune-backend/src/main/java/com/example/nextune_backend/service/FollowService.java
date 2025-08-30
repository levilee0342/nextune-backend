package com.example.nextune_backend.service;

import com.example.nextune_backend.entity.User;

import java.util.List;

public interface FollowService {
    void follow(String followerId, String followingId);
    void unfollow(String followerId, String followingId);
    boolean isFollowing(String followerId, String followingId);
    List<User> getFollowers(String userId);
    List<User> getFollowing(String userId);
}
