package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.FollowDao;
import com.mobile.studydocs.model.dto.request.FollowRequest;
import com.mobile.studydocs.model.dto.request.GetFollowerRequest;
import com.mobile.studydocs.model.dto.request.ToggleNotifyEnableRequest;
import com.mobile.studydocs.model.dto.response.FollowerResponse;
import com.mobile.studydocs.model.dto.response.FollowingResponse;
import com.mobile.studydocs.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowDao followerDao;

    public void addFollower(String userId, FollowRequest followRequest) {
        followerDao.addFollower(userId, followRequest.type(), followRequest.targetId());
    }

    public void removeFollower(String userId, FollowRequest followRequest) {
        followerDao.removeFollower(userId,  followRequest.targetId(),followRequest.type());
    }

    public void toggleNotifyEnable(String userId, ToggleNotifyEnableRequest toggleNotifyEnableRequest) {
        followerDao.toggleNotification(userId, toggleNotifyEnableRequest.followingId(), toggleNotifyEnableRequest.notifyEnables());
    }

    public List<FollowerResponse> getFollowers(GetFollowerRequest getFollowerRequest) {
        List<User> users = followerDao.getFollowers(getFollowerRequest.targetId(), getFollowerRequest.type());
        return users.stream().map(user -> FollowerResponse
                        .builder()
                        .userId(user.getUserId())
                        .fullName(user.getFullName())
                        .avatarUrl(user.getAvatarUrl())
                        .build())
                .toList();
    }

    public List<FollowingResponse> getFollowings(String userId) {
        return followerDao.getFollowings(userId);
    }

    public List<String> getFCMTokensNeedNotify(String userId,String targetId, String targetType) {
        return followerDao.getFCMTokens(userId,targetId, targetType);
    }

}
