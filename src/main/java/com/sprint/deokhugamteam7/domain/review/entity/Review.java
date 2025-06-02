package com.sprint.deokhugamteam7.domain.review.entity;


import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import com.sprint.deokhugamteam7.domain.user.entity.User;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

  @Column(updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @Column(updatable = false)
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> commentList;

  @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReviewLike> reviewLikeList;

  public static Review create(Book book, User user, String content) {
    Review review = new Review();
    review.book = book;
    review.user = user;
    review.content = content;
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
}
