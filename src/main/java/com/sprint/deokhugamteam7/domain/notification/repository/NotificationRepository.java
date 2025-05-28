package com.sprint.deokhugamteam7.domain.notification.repository;

import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  @Query("SELECT n FROM Notification n "
      + "JOIN FETCH n.user u "
      + "JOIN FETCH n.review r "
      + "WHERE n.user.id = :user_id")
  List<Notification> findAllByUserId(UUID user_id);
}
