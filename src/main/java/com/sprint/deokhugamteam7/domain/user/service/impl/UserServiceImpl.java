package com.sprint.deokhugamteam7.domain.user.service.impl;

import com.sprint.deokhugamteam7.domain.user.dto.request.UserLoginRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserRegisterRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserUpdateRequest;
import com.sprint.deokhugamteam7.domain.user.dto.response.UserDto;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.domain.user.service.UserService;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.user.UserException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserDto register(UserRegisterRequest request) {
    log.info("회원가입 요청: email={}, nickname={}", request.email(), request.nickname());

    if (userRepository.existsByEmail(request.email())) {
      log.warn("회원가입 실패: 이미 존재하는 이메일={}", request.email());
      throw new UserException(ErrorCode.DUPLICATE_EMAIL);
    }

    String encodedPassword = passwordEncoder.encode(request.password());

    User user = User.create(
        request.email(),
        request.nickname(),
        encodedPassword
    );

    User savedUser = userRepository.save(user);
    log.info("회원가입 성공: userId={}", savedUser.getId());

    return UserDto.from(savedUser);
  }

  public UserDto login(UserLoginRequest request) {
    log.info("로그인 요청: email={}", request.email());

    User user = userRepository.findByEmailIsDeletedFalse(request.email())
        .orElseThrow(() -> {
          log.warn("로그인 실패: 존재하지 않는 이메일={}", request.email());
          return new UserException(ErrorCode.USER_NOT_FOUND);
        });

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      log.warn("로그인 실패: 비밀번호 불일치");
      throw new UserException(ErrorCode.INTERNAL_BAD_REQUEST);
    }

    log.info("로그인 성공: email={}, userId={}", user.getEmail(), user.getId());
    return UserDto.from(user);
  }

  @Transactional(readOnly = true)
  public UserDto findById(UUID id) {
    log.info("사용자 조회 요청: userId={}", id);

    User user = userRepository.findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> {
          log.warn("사용자 조회 실패: 존재하지 않거나 삭제된 userId={}", id);
          return new UserException(ErrorCode.USER_NOT_FOUND);
        });

    log.info("사용자 조회 성공: userId={}", user.getId());
    return UserDto.from(user);
  }

  public UserDto update(UUID id, UserUpdateRequest request) {
    log.info("사용자 수정 요청: userId={}", id);

    User user = userRepository.findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> {
          log.warn("수정 실패: 존재하지 않거나 삭제된 userId={}", id);
          return new UserException(ErrorCode.USER_NOT_FOUND);
        });

    user.update(request.nickname()); // 닉네임만 변경

    log.info("사용자 정보 수정 성공: userId={}, updatedNickname={}", id, user.getNickname());
    return UserDto.from(user);
  }

  public void softDeleteById(UUID id) {
    log.info("소프트 삭제 요청: userId={}", id);

    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("소프트 삭제 실패: 존재하지 않는 userId={}", id);
          return new UserException(ErrorCode.USER_NOT_FOUND);
        });

    user.softDelete();
    log.info("소프트 삭제 완료: userId={}", id);
  }

  public void hardDeleteById(UUID id) {
    log.info("하드 삭제 요청: userId={}", id);

    if (!userRepository.existsById(id)) {
      log.warn("하드 삭제 실패: 존재하지 않는 userId={}", id);
      throw new UserException(ErrorCode.USER_NOT_FOUND);
    }

    userRepository.deleteById(id);
  }
}
