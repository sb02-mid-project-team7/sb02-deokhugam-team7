package com.sprint.deokhugamteam7.domain.notification.service.impl;

import com.sprint.deokhugamteam7.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationDto;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationUpdateRequest;
import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
import com.sprint.deokhugamteam7.domain.notification.repository.NotificationRepository;
import com.sprint.deokhugamteam7.domain.notification.service.NotificationService;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.notification.NotificationException;
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

    System.out.println(notificationRepository.findAll());


    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationException(ErrorCode.INTERNAL_SERVER_ERROR));

    notification.validateUserAuthorization(userId);

    notification.updateConfirmed(request.confirmed());

    NotificationDto result = new NotificationDto(
        notification.getId(),
        notification.getUser().getId(),
        notification.getReview().getId(),
        notification.getReview().getUser().getNickname(),
        notification.getContent(),
        notification.getConfirmed(),
        notification.getCreated_at(),
        notification.getUpdated_at()
    );

    return result;
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
