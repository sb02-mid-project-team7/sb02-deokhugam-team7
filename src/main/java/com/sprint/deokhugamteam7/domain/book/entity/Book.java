package com.sprint.deokhugamteam7.domain.book.entity;

import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "books")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Setter
  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted = false;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "author", nullable = false)
  private String author;

  @Column(name = "publisher", nullable = false)
  private String publisher;

  @Column(name = "published_date", nullable = false)
  private LocalDate publishedDate;

  @Column(name = "description")
  private String description;

  @Column(name = "isbn")
  private String isbn;

  @Column(name = "thumbnail_url")
  private String thumbnailUrl;

  @Setter
  @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RankingBook> rankingBooks = new ArrayList<>();

  @Setter
  @OneToMany(mappedBy = "book")
  private List<Review> reviews;

  @Builder(builderMethodName = "of")
  private Book(String title, String author, String description, String publisher,
      LocalDate publishedDate, String isbn, String thumbnailUrl) {
    validate(title, author, publisher, publishedDate);
    this.title = title;
    this.author = author;
    this.description = description;
    this.publisher = publisher;
    this.publishedDate = publishedDate;
    this.isbn = isbn;
    this.thumbnailUrl = thumbnailUrl;
  }

  public static BookBuilder create(String title, String author, String publisher,
      LocalDate publishedDate) {
    return Book.of()
        .title(title)
        .author(author)
        .publisher(publisher)
        .publishedDate(publishedDate);
  }

  private String updateField(String original, String newField) {
    return (newField != null && !newField.equals(original) ? newField.trim() : original);
  }

  private LocalDate updateField(LocalDate original, LocalDate newField) {
    return (newField != null && !newField.equals(original) ? newField : original);
  }

  public void update(String newTitle, String newAuthor, String newDescription, String newPublisher,
      LocalDate newPublisherDate, String newThumbnailUrl) {
    title = updateField(title, newTitle);
    author = updateField(author, newAuthor);
    description = updateField(description, newDescription);
    publisher = updateField(publisher, newPublisher);
    publishedDate = updateField(publishedDate, newPublisherDate);
    thumbnailUrl = updateField(thumbnailUrl, newThumbnailUrl);
  }

  private void validate(String title, String author, String publisher, LocalDate publishedDate) {
    if (title == null | author == null | publisher == null | publishedDate == null) {
      throw new DeokhugamException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  public List<Review> getReviewsWithIsDeletedIsFalse() {
    return this.reviews.stream().filter(review -> !review.getIsDeleted()).toList();
  }
}
