package com.sprint.deokhugamteam7.domain.notification.repository;

import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
import com.sprint.deokhugamteam7.domain.notification.repository.custom.NotificationRepositoryCustom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID>,
    NotificationRepositoryCustom {

  @Query("SELECT n FROM Notification n WHERE n.user.id = :user_id AND n.isDelete = false ")
  List<Notification> findByUserId(@Param("user_id") UUID userId);

  @Modifying
  @Query("UPDATE Notification n SET n.confirmed = true WHERE n.review.user.id = :user_id")
  void bulkUpdateConfirmed(@Param("user_id") UUID userId);

  @Modifying
  @Query("UPDATE Notification n SET n.isDelete = true  WHERE n.isDelete = false AND n.created_at < :threshold")
  void softDeleteOldNotifications(@Param("threshold") LocalDateTime threshold);
}
