package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.UserDao;
import com.mobile.studydocs.model.dto.FollowerDTO;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDao userDao;
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
    public boolean follow(FollowerDTO followerDTO) {
        return userDao.addFollow(followerDTO.getFolloweeId(), followerDTO.getFollowerId());
    }
    public boolean unfollow(FollowerDTO followerDTO) {
        return userDao.removeFollow(followerDTO.getFolloweeId(), followerDTO.getFollowerId());
    }
}
