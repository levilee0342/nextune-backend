package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.entity.*;
import com.example.nextune_backend.repository.FollowRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    public void follow(String followerId, String followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }

        FollowId id = new FollowId(followerId, followingId);
        if (followRepository.existsById(id)) {
            throw new IllegalStateException("Already following");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("Following user not found"));

        Follow follow = new Follow();
        follow.setId(id);
        follow.setFollowerUser(follower);
        follow.setFollowingUser(following);
        follow.setStatus(Status.FOLLOWED);

        followRepository.save(follow);
    }

    @Override
    public void unfollow(String followerId, String followingId) {
        FollowId id = new FollowId(followerId, followingId);

        Follow follow = followRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Follow relationship does not exist"));

        // Cập nhật trạng thái thành UNFOLLOWED
        follow.setStatus(Status.UNFOLLOWED);
        followRepository.save(follow);
    }


    @Override
    public boolean isFollowing(String followerId, String followingId) {
        return followRepository.existsById(new FollowId(followerId, followingId));
    }

    @Override
    public List<User> getFollowers(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return followRepository.findAllByFollowingUser(user)
                .stream()
                .map(Follow::getFollowerUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getFollowing(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return followRepository.findAllByFollowerUser(user)
                .stream()
                .map(Follow::getFollowingUser)
                .collect(Collectors.toList());
    }
}
