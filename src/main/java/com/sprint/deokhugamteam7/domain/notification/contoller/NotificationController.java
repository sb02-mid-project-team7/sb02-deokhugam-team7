package com.sprint.deokhugamteam7.domain.notification.contoller;

import com.sprint.deokhugamteam7.domain.notification.dto.NotificationDto;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationUpdateRequest;
import com.sprint.deokhugamteam7.domain.notification.service.NotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/notifications")
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PatchMapping("/{notificationId}")
    public ResponseEntity<NotificationDto> update(
        @PathVariable("notificationId") UUID notificationId,
        @RequestBody NotificationUpdateRequest request,
        @RequestHeader("Deokhugam-Request-User-ID") UUID userId
    ) {
        NotificationDto notificationDto = notificationService.update(notificationId, userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(notificationDto);
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> updateAll(
        @RequestHeader("Deokhugam-Request-User-ID") UUID userId
    ) {
        notificationService.updateAll(userId);
        return ResponseEntity.noContent().build();
    }
}
