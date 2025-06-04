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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  @Override
  public NotificationDto update(UUID notificationId, UUID userId,
      NotificationUpdateRequest request) {

    log.info("알림 단일 수정: notificationId: {} 조회 진행", notificationId);
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> {
          log.info("알림 단일 수정: notificationId {} 조회 실패",notificationId);
          return new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND);
        });
    log.info("알림 단일 수정: notificationId: {} 조회 성공", notificationId);

    log.info("알림 단일 수정: userId: {} 조회 진행", userId);
    userRepository.findById(userId)
            .orElseThrow(() -> {
              log.info("알림 단일 수정: userId: {}, 조회 실패", userId);
              return new UserException(ErrorCode.INTERNAL_SERVER_ERROR);
            });
    log.info("알림 단일 수정: userId: {} 조회 성공", userId);

    log.info("알림 단일 수정: userId: {} 권한 검증 진행", userId);
    notification.validateUserAuthorization(userId);
    log.info("알림 단일 수정: userId: {} 권한 검증 성공", userId);

    notification.updateConfirmed(request.confirmed());
    log.info("알림 단일 수정 완료: notificationId: {}", notificationId);

    return NotificationDto.fromEntity(notification);
  }

  @Override
  public void updateAll(UUID userId) {
    log.info("알림 목록 수정: userId: {} 조회 진행", userId);
    userRepository.findById(userId)
        .orElseThrow(() -> {
          log.info("알림 목록 수정: userId: {} 조회 실패", userId);
          return new UserException(ErrorCode.INTERNAL_SERVER_ERROR);
        });
    log.info("알림 목록 수정: userId: {} 조회 성공", userId);
    notificationRepository.bulkUpdateConfirmed(userId);
    log.info("알림 목록 수정 완료");
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponseNotificationDto findAll(NotificationCursorRequest request) {
    log.info("알림 목록 조회: userId: {} 조회 진행", request.userId());
    userRepository.findById(request.userId())
        .orElseThrow(() -> {
          log.info("알림 목록 조회: userId: {} 조화 실패", request.userId());
          return new UserException(ErrorCode.INTERNAL_SERVER_ERROR);
        });
    log.info("알림 목록 조회: userId: {} 조회 성공", request.userId());

    log.info("알림 목록 조회: cursor 기반 목록 조회 진행");
    Slice<NotificationDto> sliceNotificationDto = notificationRepository.findAllByCursor(request);
    log.info("알림 목록 조회: cursor기반 알림 목록 조회 성공");

    log.info("알림 목록 조회: userId: {} 알림 목록 전체 개수 조회 진행", request.userId());
    long totalElements = notificationRepository.countAllById(request.userId());
    log.info("알림 목록 조회: userId: {} 알림 목록 전체 개수 조회 완료", request.userId());

    log.info("알림 목록 조회 완료");

    return CursorPageResponseNotificationDto.fromSlice(
        sliceNotificationDto,
        totalElements
    );
  }

//  @Scheduled(cron = "0 0 0 * * Sun")
  @Scheduled(cron = "0 0/1 * * * *")
  public void softDeleteNotificationsOlderThanAWeek() {
    log.info("알림 삭제: 주기 별 알림 삭제 진행");
    notificationRepository.softDeleteOldNotifications(LocalDateTime.now().minusWeeks(1));
    log.info("알림 삭제: 주기 별 알림 삭제 완료");
  }

}
