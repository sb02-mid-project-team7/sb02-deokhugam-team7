package com.sprint.deokhugamteam7.domain.notification.repository;

import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  @Query("SELECT n FROM Notification n "
      + "JOIN FETCH n.user u "
      + "JOIN FETCH n.review r "
      + "WHERE n.review.user.id = :user_id AND n.isDelete = false ")
  List<Notification> findAllByReviewerId(UUID user_id);

  @Modifying
  @Query("UPDATE Notification n SET n.confirmed = true WHERE n.review.user.id = :user_id")
  void bulkUpdateConfirmed(UUID user_id);

  @Modifying
  @Query("UPDATE Notification n SET n.isDelete = true  WHERE n.isDelete = false AND n.created_at < :threshold")
  void softDeleteOldNotifications(LocalDateTime threshold);
}
