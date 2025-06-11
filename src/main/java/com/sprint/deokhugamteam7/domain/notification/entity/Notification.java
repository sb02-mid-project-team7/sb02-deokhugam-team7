package com.sprint.deokhugamteam7.domain.notification.entity;

import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.notification.NotificationException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Table(name = "notifications")
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id")
  private Review review;

  private String content;

  @ColumnDefault("false")
  private boolean confirmed;

  @Column(name = "is_deleted")
  @ColumnDefault("false")
  private boolean isDelete;

  @CreatedDate
  private LocalDateTime created_at;

  @LastModifiedDate
  private LocalDateTime updated_at;

  // confirmed이 getter 어노테이션으로 인식되지 않음
  public boolean getConfirmed() {
    return confirmed;
  }

  private Notification(User user, Review review, String content) {
    this.user = user;
    this.review = review;
    this.content = content;
  }

  public static Notification create(User user, Review review, String content) {
    return new Notification(user, review, content);
  }

  public void updateConfirmed(boolean confirmed) {
    this.confirmed = confirmed;
  }

  public void validateUserAuthorization(UUID userId) {
    if (!this.review.getUser().getId().equals(userId)) {
      log.info("알람 수정: userId: {} 권한 검증 실패", userId);
      throw new NotificationException(ErrorCode.NOTIFICATION_NOT_OWNED);
    }
  }

}
