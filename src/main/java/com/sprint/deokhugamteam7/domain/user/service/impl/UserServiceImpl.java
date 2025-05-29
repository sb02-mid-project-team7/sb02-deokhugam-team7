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

    return new UserDto(
        savedUser.getId(),
        savedUser.getEmail(),
        savedUser.getNickname(),
        savedUser.getCreateAt()
    );
  }

  public UserDto login(UserLoginRequest request) {
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new UserException(ErrorCode.INTERNAL_SERVER_ERROR));

    if (!user.getPassword().equals(request.password())) {
      throw new UserException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    return new UserDto(
        user.getId(),
        user.getEmail(),
        user.getNickname(),
        user.getCreateAt()
    );
  }

  @Transactional(readOnly = true)
  public UserDto findById(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserException(ErrorCode.INTERNAL_SERVER_ERROR));

    return new UserDto(
        user.getId(),
        user.getEmail(),
        user.getNickname(),
        user.getCreateAt()
    );
  }

  public UserDto update(UUID id, UserUpdateRequest request) {
    return null;
  }

  public void softDeleteById(UUID id) {
  }

  public void hardDeleteById(UUID id) {
  }
}
