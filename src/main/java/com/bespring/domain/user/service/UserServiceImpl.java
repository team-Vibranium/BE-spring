package com.bespring.domain.user.service;

import com.bespring.domain.auth.dto.request.RegisterRequest;
import com.bespring.domain.auth.dto.response.AuthResponse;
import com.bespring.domain.user.entity.User;
import com.bespring.domain.user.repository.UserRepository;
import com.bespring.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse.UserInfo register(RegisterRequest request) {
        // 비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(hashedPassword)
                .nickname(request.getNickname())
                .points(0)
                .selectedAvatar("avatar_1")
                .build();

        User savedUser = userRepository.save(user);

        log.info("User created: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        return convertToUserInfo(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse.UserInfo getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return convertToUserInfo(user);
    }

    @Override
    public AuthResponse.UserInfo updateUserInfo(Long userId, String nickname, String selectedAvatar) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname.trim());
        }

        if (selectedAvatar != null && !selectedAvatar.trim().isEmpty()) {
            user.setSelectedAvatar(selectedAvatar.trim());
        }

        User updatedUser = userRepository.save(user);

        log.info("User updated: userId={}, nickname={}, avatar={}",
                userId, updatedUser.getNickname(), updatedUser.getSelectedAvatar());

        return convertToUserInfo(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        userRepository.delete(user);

        log.info("User deleted: userId={}, email={}", userId, user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public AuthResponse.UserInfo changeNickname(Long userId, String newNickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 닉네임 중복 확인 (본인 제외)
        if (userRepository.existsByNickname(newNickname) && !newNickname.equals(user.getNickname())) {
            throw new com.bespring.global.exception.DuplicateResourceException("이미 존재하는 닉네임입니다.");
        }

        user.setNickname(newNickname);
        User updatedUser = userRepository.save(user);

        log.info("Nickname changed: userId={}, oldNickname={}, newNickname={}",
                userId, user.getNickname(), newNickname);

        return convertToUserInfo(updatedUser);
    }

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new org.springframework.security.authentication.BadCredentialsException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 새 비밀번호 암호화
        String hashedNewPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(hashedNewPassword);

        userRepository.save(user);

        log.info("Password changed: userId={}, email={}", userId, user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    private AuthResponse.UserInfo convertToUserInfo(User user) {
        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .points(user.getPoints())
                .selectedAvatar(user.getSelectedAvatar())
                .createdAt(user.getCreatedAt())
                .build();
    }
}