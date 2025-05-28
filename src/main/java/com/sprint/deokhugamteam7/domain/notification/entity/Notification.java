package com.sprint.deokhugamteam7.domain.notification.entity;

import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Table(name = "notifications")
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne
  private User user;

  @OneToOne
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

  private Notification(User user, Review review, String content) {
    this.user = user;
    this.review = review;
    this.content = content;
  }


  public void updateConfirmed(boolean confirmed) {
    this.confirmed = confirmed;
  }


}
