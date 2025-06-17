package com.mobile.studydocs.controller;

import com.mobile.studydocs.model.dto.request.FollowRequest;
import com.mobile.studydocs.model.dto.request.GetFollowerRequest;
import com.mobile.studydocs.model.dto.request.ToggleNotifyRequest;
import com.mobile.studydocs.model.enums.FollowType;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class FollowController {
    private final FollowService followService;

    @PostMapping("/follow")
    public BaseResponse follow(@RequestAttribute("userId") String userId, @RequestBody FollowRequest followRequest) {
        return new BaseResponse(HttpStatus.OK.value(), "Theo dõi thành công", followService.addFollower(userId, followRequest));
    }

    @PostMapping("/unfollow")
    public BaseResponse unFollow(@RequestAttribute("userId") String userId, @RequestBody String followingId) {
        return new BaseResponse(HttpStatus.OK.value(), "Hủy Theo dõi thành công", followService.removeFollower(userId, followingId));
    }

    @PatchMapping("/follow")
    public BaseResponse toggleNotifyEnable(@RequestAttribute("userId") String userId, @RequestBody ToggleNotifyRequest toggleNotifyRequest) {
        followService.toggleNotifyEnable(userId, toggleNotifyRequest);
        return new BaseResponse(HttpStatus.OK.value(), "Thay đổi thông báo thành công", true);
    }

    @GetMapping("/follower")
    public BaseResponse getFollowers(@RequestParam("targetId") String targetId, @RequestParam("type") FollowType type) {
        GetFollowerRequest getFollowerRequest = new GetFollowerRequest(targetId, type);
        return new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách người theo dõi thành công", followService.getFollowers(getFollowerRequest));
    }

    @GetMapping("/following")
    public BaseResponse getFollowings(@RequestAttribute("userId") String userId) {
        return new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách người đang theo dõi thành công", followService.getFollowings(userId));
    }


}
