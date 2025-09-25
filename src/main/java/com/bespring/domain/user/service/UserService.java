package com.bespring.domain.user.service;

import com.bespring.domain.auth.dto.request.RegisterRequest;
import com.bespring.domain.auth.dto.response.AuthResponse;
import com.bespring.domain.user.entity.User;

import java.util.Optional;

public interface UserService {

    AuthResponse.UserInfo register(RegisterRequest request);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    AuthResponse.UserInfo getUserInfo(Long userId);

    AuthResponse.UserInfo updateUserInfo(Long userId, String nickname, String selectedAvatar);

    AuthResponse.UserInfo changeNickname(Long userId, String newNickname);

    void changePassword(Long userId, String currentPassword, String newPassword);

    void deleteUser(Long userId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}