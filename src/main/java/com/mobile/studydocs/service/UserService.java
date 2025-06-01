package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.UserDao;
import com.mobile.studydocs.exception.BusinessException;
import com.mobile.studydocs.exception.ResourceNotFoundException;
import com.mobile.studydocs.model.dto.FollowerDTO;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean follow(FollowerDTO followerDTO) {
        if (followerDTO.getFolloweeId() == null || followerDTO.getFollowerId() == null) {
            throw new BusinessException("Mã Id không thể để trống", "INVALID_FOLLOW_DATA");
        }
        if (followerDTO.getFolloweeId().equals(followerDTO.getFollowerId())) {
            throw new BusinessException("Người dùng không thể tự theo dõi bản thân", "SELF_FOLLOW_NOT_ALLOWED");
        }

        // Kiểm tra user tồn tại
        if (!userDao.exists(followerDTO.getFolloweeId())) {
            throw new ResourceNotFoundException("User", "id", followerDTO.getFolloweeId());
        }

        return userDao.addFollow(followerDTO.getFolloweeId(), followerDTO.getFollowerId());
    }

    public boolean unfollow(FollowerDTO followerDTO) {
        if (followerDTO.getFolloweeId() == null || followerDTO.getFollowerId() == null) {
            throw new BusinessException("Mã Id không thể để trống", "INVALID_FOLLOW_DATA");
        }
        if (followerDTO.getFolloweeId().equals(followerDTO.getFollowerId())) {
            throw new BusinessException("Người dùng không thể tự hủy theo dõi bản thân", "SELF_UNFOLLOW_NOT_ALLOWED");
        }

        // Kiểm tra user tồn tại
        if (!userDao.exists(followerDTO.getFolloweeId())) {
            throw new ResourceNotFoundException("User", "id", followerDTO.getFolloweeId());
        }

        return userDao.removeFollow(followerDTO.getFolloweeId(), followerDTO.getFollowerId());
    }
}
