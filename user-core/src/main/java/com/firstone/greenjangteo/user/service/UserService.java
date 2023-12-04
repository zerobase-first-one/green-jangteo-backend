package com.firstone.greenjangteo.user.service;

import com.firstone.greenjangteo.user.model.entity.User;

/**
 * 회원 서비스
 */
public interface UserService {
    User getUser(Long id);
}
