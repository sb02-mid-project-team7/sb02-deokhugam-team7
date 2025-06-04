package com.sprint.deokhugamteam7.domain.review.entity;


import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.review.ReviewException;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private int rating;

  @Column(nullable = false)
  private Boolean isDeleted;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime updatedAt;

  public static Review create(Book book, User user, String content, int rating) {
    Review review = new Review();
    review.book = book;
    review.user = user;
    review.content = content;
    review.rating = rating;
    review.isDeleted = false;

    return review;
  }

  public void delete() {
    this.isDeleted = true;
  }

  public void update(String content, int rating) {
    this.content = content;
    this.rating = rating;
  }

  public void validateUserAuthorization(UUID userId) {
    if (!this.user.getId().equals(userId)) {
      throw new ReviewException(ErrorCode.REVIEW_NOT_OWNED);
    }
  }
}
