package com.sprint.deokhugamteam7.domain.notification.service;

import com.sprint.deokhugamteam7.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationCursorRequest;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationDto;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  NotificationDto update(UUID notificationId, UUID userId, NotificationUpdateRequest request);

  void updateAll(UUID userId);

  CursorPageResponseNotificationDto findAll(NotificationCursorRequest request);
}
