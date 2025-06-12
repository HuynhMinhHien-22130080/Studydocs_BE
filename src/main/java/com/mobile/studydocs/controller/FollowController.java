package com.mobile.studydocs.controller;

import com.mobile.studydocs.model.dto.request.FollowRequest;
import com.mobile.studydocs.model.dto.request.GetFollowerRequest;
import com.mobile.studydocs.model.dto.request.ToggleNotifyEnableRequest;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/follow")
    public BaseResponse follow(@RequestAttribute("userId") String userId, @RequestBody FollowRequest followRequest) {
        followService.addFollower(userId, followRequest);
        return new BaseResponse(HttpStatus.OK.value(), "Theo dõi thành công", true);
    }

    @PostMapping("/unfollow")
    public BaseResponse unFollow(@RequestAttribute("userId") String userId, @RequestBody FollowRequest followRequest) {
        followService.removeFollower(userId, followRequest);
        return new BaseResponse(HttpStatus.OK.value(), "Hủy Theo dõi thành công", true);
    }

    @PatchMapping("/follow")
    public BaseResponse toggleNotifyEnable(@RequestAttribute("userId") String userId, @RequestBody ToggleNotifyEnableRequest toggleNotifyEnableRequest) {
        followService.toggleNotifyEnable(userId, toggleNotifyEnableRequest);
        return new BaseResponse(HttpStatus.OK.value(), "Thay đổi thông báo thành công", true);
    }

    @GetMapping("/follower")
    public BaseResponse getFollowers(@RequestBody GetFollowerRequest getFollowerRequest) {
        return new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách người theo dõi thành công", followService.getFollowers(getFollowerRequest));
    }

    @GetMapping("/following")
    public BaseResponse getFollowings(@RequestAttribute("userId") String userId) {
        return new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách người đang theo dõi thành công", followService.getFollowings(userId));
    }


}
