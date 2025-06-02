package com.sprint.deokhugamteam7.domain.notification.repository.custom;

import com.sprint.deokhugamteam7.domain.notification.dto.NotificationCursorRequest;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationDto;
import java.util.UUID;
import org.springframework.data.domain.Slice;

public interface NotificationRepositoryCustom {
    Slice<NotificationDto> findAllByCursor(NotificationCursorRequest request);
    long countAllById(UUID userId);
}
