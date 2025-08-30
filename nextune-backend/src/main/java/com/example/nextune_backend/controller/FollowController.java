package com.example.nextune_backend.controller;

import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.service.FollowService;
import com.example.nextune_backend.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserUtility securityUtil;

    @PostMapping("/{followingId}")
    public ResponseEntity<String> follow(@PathVariable String followingId) {
        String userId = securityUtil.getCurrentUserId();
        followService.follow(userId, followingId);
        return ResponseEntity.ok("Followed user with id: " + followingId);
    }

    @DeleteMapping("/{followingId}")
    public ResponseEntity<String> unfollow(@PathVariable String followingId) {
        String userId = securityUtil.getCurrentUserId();
        followService.unfollow(userId, followingId);
        return ResponseEntity.ok("Unfollowed user with id: " + followingId);
    }

    @GetMapping("/is-following/{followingId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable String followingId) {
        String userId = securityUtil.getCurrentUserId();
        boolean result = followService.isFollowing(userId, followingId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/followers")
    public ResponseEntity<List<User>> getFollowers() {
        String userId = securityUtil.getCurrentUserId();
        List<User> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/following")
    public ResponseEntity<List<User>> getFollowing() {
        String userId = securityUtil.getCurrentUserId();
        List<User> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }
}
