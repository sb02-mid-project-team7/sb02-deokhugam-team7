package com.sprint.deokhugamteam7.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true, length = 30)
  private String email;

  @Column(nullable = false, length = 20)
  private String nickname;

  @Column(nullable = false)
  private String password;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updateAt;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted = false;

  public static User create(String email, String nickname, String password) {
    User user = new User();
    user.email = email;
    user.nickname = nickname;
    user.password = password;
    user.isDeleted = false;
    return user;
  }

  public void update(String nickname) {
    this.nickname = nickname;
  }

  public void softDelete() {
    this.isDeleted = true;
  }
}
