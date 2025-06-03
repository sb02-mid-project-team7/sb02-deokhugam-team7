package com.sprint.deokhugamteam7.domain.user.service.impl;

import com.sprint.deokhugamteam7.domain.user.dto.request.UserLoginRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserRegisterRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserUpdateRequest;
import com.sprint.deokhugamteam7.domain.user.dto.response.UserDto;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.domain.user.service.UserService;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.user.UserException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserDto register(UserRegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new UserException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    User user = User.create(
        request.email(),
        request.nickname(),
        request.password()
    );

    User savedUser = userRepository.save(user);

    return UserDto.from(savedUser);
  }

  public UserDto login(UserLoginRequest request) {
    User user = userRepository.findByEmailIsDeletedFalse(request.email())
        .orElseThrow(() -> new UserException(ErrorCode.INTERNAL_SERVER_ERROR));

    if (!user.getPassword().equals(request.password())) {
      throw new UserException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    return UserDto.from(user);
  }

  @Transactional(readOnly = true)
  public UserDto findById(UUID id) {
    User user = userRepository.findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> new UserException(ErrorCode.INTERNAL_SERVER_ERROR));

    return UserDto.from(user);
  }

  public UserDto update(UUID id, UserUpdateRequest request) {
    User user = userRepository.findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> new UserException(ErrorCode.INTERNAL_SERVER_ERROR));

    user.update(request.nickname()); // 닉네임만 변경

    return UserDto.from(user);
  }

  public void softDeleteById(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new DeokhugamException(ErrorCode.INTERNAL_SERVER_ERROR));

    user.softDelete();
  }

  public void hardDeleteById(UUID id) {
    if (!userRepository.existsById(id)) {
      throw new DeokhugamException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    userRepository.deleteById(id);
  }
}
