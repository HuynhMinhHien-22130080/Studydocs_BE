package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.FollowDao;
import com.mobile.studydocs.model.dto.request.FollowRequest;
import com.mobile.studydocs.model.dto.request.GetFollowerRequest;
import com.mobile.studydocs.model.dto.request.ToggleNotifyRequest;
import com.mobile.studydocs.model.dto.response.FollowerResponse;
import com.mobile.studydocs.model.dto.response.FollowingResponse;
import com.mobile.studydocs.model.entity.User;
import com.mobile.studydocs.model.enums.FollowType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowDao followerDao;

    public void addFollower(String userId, FollowRequest followRequest) {
        followerDao.addFollower(userId, followRequest.type(), followRequest.targetId());
    }

    public String removeFollower(String userId, String followingId) {
        followerDao.removeFollower(userId, followingId);
        return followingId;
    }

    public void toggleNotifyEnable(String userId, ToggleNotifyRequest toggleNotifyRequest) {
        followerDao.toggleNotification(userId, toggleNotifyRequest.followingId(), toggleNotifyRequest.notifyEnable());
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

    public Map<String, List<String>> getFCMTokensNeedNotify(String targetId, FollowType targetType) {
        return followerDao.getFCMTokens(targetId, targetType);
    }

    public void removeFollowerByTarget(String userId, FollowRequest unfollowRequest) {
        String followingId = followerDao.getFollowingId(userId, unfollowRequest.targetId(), unfollowRequest.type());
        followerDao.removeFollower(userId, followingId);
        if (followingId == null) {
            throw new IllegalArgumentException("Không tìm thấy following với targetId: " + unfollowRequest.targetId());
        }
    }
}
