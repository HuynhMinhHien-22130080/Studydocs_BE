package com.mobile.studydocs.controller;

import com.mobile.studydocs.model.dto.FollowerDTO;
import com.mobile.studydocs.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/follow")
    public ResponseEntity<Boolean> follow(@RequestBody FollowerDTO followerDTO) {
        return ResponseEntity.ok(userService.follow(followerDTO));
    }

    @RequestMapping("/unfollow")
    public ResponseEntity<Boolean> unfollow(@RequestBody FollowerDTO followerDTO) {
        return ResponseEntity.ok(userService.unfollow(followerDTO));
    }
}
