package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.UserDao;
import com.mobile.studydocs.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;
    public User findUserById(String userId){
        return userDao.getUser(userId);
    }
}
