package com.sprint.deokhugamteam7.domain.comment.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.domain.comment.dto.CommentDto;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentCreateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentUpdateRequest;
import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import com.sprint.deokhugamteam7.domain.comment.repository.CommentRepository;
import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
import com.sprint.deokhugamteam7.domain.notification.repository.NotificationRepository;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	@Mock
	private CommentRepository commentRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ReviewRepository reviewRepository;
	@Mock
	private NotificationRepository notificationRepository;

	@InjectMocks
	private CommentService commentService;

	@Test
	@DisplayName("create 성공 케이스")
	void createTest() {
		// given
		UUID userId = UUID.randomUUID();
		UUID reviewId = UUID.randomUUID();
		String content = "new content";
		CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, content);

		User mockUser = Mockito.mock(User.class);
		Review mockReview = Mockito.mock(Review.class);
		User mockReviewOwner = Mockito.mock(User.class);

		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(mockReview));
		when(mockUser.isDeleted()).thenReturn(false);
		when(mockReview.getIsDeleted()).thenReturn(false);
		when(mockReview.getUser()).thenReturn(mockReviewOwner);

		when(commentRepository.save(any(Comment.class))).thenAnswer(
			invocation -> invocation.getArgument(0));
		when(notificationRepository.save(any(Notification.class))).thenAnswer(
			invocation -> invocation.getArgument(0));

		// when
		CommentDto result = commentService.create(request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.content()).isEqualTo(content);

		verify(commentRepository).save(any(Comment.class));
		verify(notificationRepository).save(any(Notification.class));
	}

	@Test
	@DisplayName("생성 실패 - user id 검증 실패")
	void createTest2() {
		// given
		UUID userId = UUID.randomUUID();
		UUID reviewId = UUID.randomUUID();
		CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, "내용");

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> commentService.create(request))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessage("user not found");
	}

	@Test
	@DisplayName("update 성공 케이스")
	void updateTest() {
		// given
		UUID commentId = UUID.randomUUID();
		UUID correctUserId = UUID.randomUUID();
		String newContent = "수정된 내용입니다.";
		CommentUpdateRequest request = new CommentUpdateRequest(newContent);

		User mockUser = Mockito.mock(User.class);
		Review mockReview = Mockito.mock(Review.class);
		Comment mockComment = Mockito.mock(Comment.class);

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));
		when(mockComment.getUser()).thenReturn(mockUser);
		when(mockUser.getId()).thenReturn(correctUserId);
		when(mockComment.getReview()).thenReturn(mockReview);
		when(mockComment.getId()).thenReturn(commentId);
		when(mockReview.getId()).thenReturn(UUID.randomUUID());
		when(mockUser.getNickname()).thenReturn("테스트유저");
		when(mockComment.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(1));
		when(mockComment.getContent()).thenReturn(newContent);
		when(mockComment.getUpdatedAt()).thenReturn(LocalDateTime.now());

		// when
		CommentDto resultDto = commentService.update(commentId, correctUserId, request);

		// then
		verify(mockComment).update(newContent);

		assertThat(resultDto).isNotNull();
		assertThat(resultDto.id()).isEqualTo(commentId);
		assertThat(resultDto.content()).isEqualTo(newContent);
	}

	@Test
	@DisplayName("update 실패 케이스 - 수정 권한이 없을 때")
	void updateTest2() {
		// given
		UUID commentId = UUID.randomUUID();
		UUID correctUserId = UUID.randomUUID();
		UUID wrongUserId = UUID.randomUUID();
		CommentUpdateRequest request = new CommentUpdateRequest("수정 시도");

		User mockUser = Mockito.mock(User.class);
		Comment mockComment = Mockito.mock(Comment.class);

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));
		when(mockComment.getUser()).thenReturn(mockUser);
		when(mockUser.getId()).thenReturn(correctUserId);

		// when & then
		assertThatThrownBy(() -> commentService.update(commentId, wrongUserId, request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당 댓글을 수정할 권한이 없습니다.");
	}

	@Test
	@DisplayName("실패 케이스 - 내용이 공백일 때")
	void update_comment_fail_content_is_blank() {
		// given
		UUID commentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		String blankContent = "   ";
		CommentUpdateRequest request = new CommentUpdateRequest(blankContent);

		User mockUser = Mockito.mock(User.class);
		Comment mockComment = Mockito.mock(Comment.class);

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));
		when(mockComment.getUser()).thenReturn(mockUser);
		when(mockUser.getId()).thenReturn(userId);

		// when & then
		assertThatThrownBy(() -> commentService.update(commentId, userId, request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("댓글은 공백일 수 없습니다.");
	}
}
