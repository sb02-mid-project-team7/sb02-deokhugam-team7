package com.sprint.deokhugamteam7.domain.notification.service.impl;

import com.sprint.deokhugamteam7.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationCursorRequest;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationDto;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationUpdateRequest;
import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
import com.sprint.deokhugamteam7.domain.notification.repository.NotificationRepository;
import com.sprint.deokhugamteam7.domain.notification.service.NotificationService;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.notification.NotificationException;
import com.sprint.deokhugamteam7.exception.user.UserException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  @Override
  public NotificationDto update(UUID notificationId, UUID userId,
      NotificationUpdateRequest request) {

    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND));

    userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.INTERNAL_SERVER_ERROR));

    notification.validateUserAuthorization(userId);
    notification.updateConfirmed(request.confirmed());

    return NotificationDto.fromEntity(notification);
  }

  @Override
  public void updateAll(UUID userId) {
    userRepository.findById(userId)
        .orElseThrow(() -> new UserException(ErrorCode.INTERNAL_SERVER_ERROR));
    notificationRepository.bulkUpdateConfirmed(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponseNotificationDto findAll(NotificationCursorRequest request) {
    userRepository.findById(request.userId())
        .orElseThrow(() -> new UserException(ErrorCode.INTERNAL_SERVER_ERROR));

    Slice<NotificationDto> sliceNotificationDto = notificationRepository.findAllByCursor(request);
    long totalElements = notificationRepository.countAllById(request.userId());

    return CursorPageResponseNotificationDto.fromSlice(
        sliceNotificationDto,
        totalElements
    );
  }

  @Scheduled(cron = "0 0 0 * * Sun")
  public void softDeleteNotificationsOlderThanAWeek() {
    notificationRepository.softDeleteOldNotifications(LocalDateTime.now().minusWeeks(1));
  }

}
