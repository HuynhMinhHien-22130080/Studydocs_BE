package com.mobile.studydocs.controller;

import com.mobile.studydocs.model.dto.FollowerDTO;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/follow")
    public ResponseEntity<BaseResponse> follow(@RequestBody FollowerDTO followerDTO) {
        Boolean isFollowed = userService.follow(followerDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Follow thành công", isFollowed));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<BaseResponse> unfollow(@RequestBody FollowerDTO followerDTO) {
        Boolean isUnFollowed = userService.unfollow(followerDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Unfollow thành công", isUnFollowed));
    }
}
