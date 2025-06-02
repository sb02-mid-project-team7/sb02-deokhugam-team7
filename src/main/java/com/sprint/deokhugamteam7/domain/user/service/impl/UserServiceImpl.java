package com.sprint.deokhugamteam7.domain.user.service.impl;

import com.sprint.deokhugamteam7.domain.user.dto.request.UserLoginRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserRegisterRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserUpdateRequest;
import com.sprint.deokhugamteam7.domain.user.dto.response.UserDto;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.domain.user.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserDto register(UserRegisterRequest request) {
    return null;
  }

  public UserDto login(UserLoginRequest request) {
    return null;
  }

  public UserDto findById(UUID id) {
    return null;
  }

  public UserDto update(UUID id, UserUpdateRequest request) {
    return null;
  }

  public void softDeleteById(UUID id) {
  }

  public void hardDeleteById(UUID id) {
  }
}
