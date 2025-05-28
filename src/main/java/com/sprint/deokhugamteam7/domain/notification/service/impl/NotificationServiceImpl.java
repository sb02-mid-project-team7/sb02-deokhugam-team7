package com.sprint.deokhugamteam7.domain.notification.service.impl;

import com.sprint.deokhugamteam7.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationDto;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationUpdateRequest;
import com.sprint.deokhugamteam7.domain.notification.repository.NotificationRepository;
import com.sprint.deokhugamteam7.domain.notification.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;

  @Override
  public NotificationDto update(UUID notificationId, UUID userId,
      NotificationUpdateRequest request) {
    return null;
  }

  @Override
  public List<NotificationDto> updateAll(UUID userId) {
    notificationRepository.findAllByUserId(userId);
    return List.of();
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponseNotificationDto findAll(UUID userId) {
    notificationRepository.findAllByUserId(userId);
    return null;
  }

}
