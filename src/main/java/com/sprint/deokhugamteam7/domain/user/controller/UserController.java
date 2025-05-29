package com.sprint.deokhugamteam7.domain.user.controller;

import com.sprint.deokhugamteam7.domain.user.dto.request.UserLoginRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserRegisterRequest;
import com.sprint.deokhugamteam7.domain.user.dto.request.UserUpdateRequest;
import com.sprint.deokhugamteam7.domain.user.dto.response.UserDto;
import com.sprint.deokhugamteam7.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegisterRequest request) {
    UserDto user = userService.register(request);
    return ResponseEntity.status(201).body(user);
  }

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest request, HttpServletRequest httpRequest) {
    UserDto user = userService.login(request);

    // 기존 세션 무효화
    httpRequest.getSession().invalidate();
    // 새 세션 생성
    httpRequest.getSession(true).setAttribute("userId", user.id().toString());

    return ResponseEntity.status(200).body(user);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> findById(@PathVariable UUID id) {
    UserDto user = userService.findById(id);
    return ResponseEntity.status(200).body(user);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserDto> update(@PathVariable UUID id,
      @Valid @RequestBody UserUpdateRequest request) {
    UserDto updated = userService.update(id, request);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> softDelete(@PathVariable UUID id) {
    userService.softDeleteById(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}/hard")
  public ResponseEntity<Void> hardDelete(@PathVariable UUID id) {
    userService.hardDeleteById(id);
    return ResponseEntity.noContent().build();
  }
}
