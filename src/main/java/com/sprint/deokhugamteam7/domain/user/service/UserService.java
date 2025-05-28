package com.sprint.deokhugamteam7.domain.user.service;

import com.sprint.deokhugamteam7.domain.user.dto.request.UserLoginRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserRegisterRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserUpdateRequest;
import com.sprint.deokhugamteam7.domain.user.dto.response.UserDto;
import java.util.UUID;

public interface UserService {

  UserDto register(UserRegisterRequest request);

  UserDto login(UserLoginRequest request);

  UserDto findById(UUID id);

  UserDto update(UUID id, UserUpdateRequest request);

  void softDeleteById(UUID id);

  void hardDeleteById(UUID id);
}
