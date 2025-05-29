package com.sprint.deokhugamteam7.domain.review.entity;


import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.review.ReviewException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "reviews")
@NoArgsConstructor
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

  // 두 List가 필요없다는게 확인 됨. 그래도 혹시 모르니, 기능 구현이 끝나기 전까진 놔둘 예정.
  @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> commentList;

  @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReviewLike> reviewLikeList;

  public static Review create(Book book, User user, String content, int rating) {
    Review review = new Review();
    review.book = book;
    review.user = user;
    review.content = content;
    review.rating = rating;
    review.isDeleted = false;
    review.commentList = new ArrayList<>();
    review.reviewLikeList = new ArrayList<>();

    return review;
  }

  public void addComment(Comment comment) {
    commentList.add(comment);
    comment.setReview(this);
  }

  public void addReviewLike(ReviewLike reviewLike) {
    reviewLikeList.add(reviewLike);
    reviewLike.setReview(this);
  }

  public void delete() {
    isDeleted = true;
  }

  public void update(String content, int rating) {
    this.content = content;
    this.rating = rating;
  }

  public void validateUserAuthorization(UUID userId) {
    if (!this.user.getId().equals(userId)) {
      throw new ReviewException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }
}
