package com.sprint.deokhugamteam7.domain.user.service;

import com.sprint.deokhugamteam7.domain.user.dto.response.CursorPageResponsePowerUserDto;

public interface PowerUserService {

  CursorPageResponsePowerUserDto getPowerUsers();

  void dailyScores();

  void weeklyScores();

  void monthlyScores();
}
