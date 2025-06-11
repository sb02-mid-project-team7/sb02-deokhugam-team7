package com.sprint.deokhugamteam7.domain.comment.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.domain.comment.dto.CommentDto;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentCreateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentUpdateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.response.CursorPageResponseCommentDto;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

	@Nested
	@DisplayName("댓글 생성(create) 실패 테스트")
	class CreateCommentFailTest {

		@Test
		@DisplayName("삭제된 사용자가 댓글 생성 시도")
		void createFailsWhenUserIsDeleted() {
			// given
			CommentCreateRequest request = new CommentCreateRequest(UUID.randomUUID(),
				UUID.randomUUID(), "내용");
			User mockUser = Mockito.mock(User.class);
			when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(mockUser));
			when(mockUser.isDeleted()).thenReturn(true);

			// when & then
			assertThatThrownBy(() -> commentService.create(request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("user is deleted");
		}

		@Test
		@DisplayName("삭제된 리뷰에 댓글 생성 시도")
		void createFailsWhenReviewIsDeleted() {
			// given
			CommentCreateRequest request = new CommentCreateRequest(UUID.randomUUID(),
				UUID.randomUUID(), "내용");
			User mockUser = Mockito.mock(User.class);
			Review mockReview = Mockito.mock(Review.class);
			when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(mockUser));
			when(mockUser.isDeleted()).thenReturn(false);
			when(reviewRepository.findById(any(UUID.class))).thenReturn(Optional.of(mockReview));
			when(mockReview.getIsDeleted()).thenReturn(true);

			// when & then
			assertThatThrownBy(() -> commentService.create(request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("review is deleted");
		}
	}

	@Nested
	@DisplayName("댓글 삭제(deleteSoft) 테스트")
	class DeleteSoftCommentTest {

		@Test
		@DisplayName("성공 케이스")
		void deleteSoftSuccess() {
			// given
			UUID commentId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			User mockUser = Mockito.mock(User.class);
			Comment mockComment = Mockito.mock(Comment.class);

			when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));
			when(mockComment.getUser()).thenReturn(mockUser);
			when(mockUser.getId()).thenReturn(userId);

			// when
			commentService.deleteSoft(commentId, userId);

			// then
			verify(mockComment).delete();
		}

		@Test
		@DisplayName("실패 케이스 - 댓글을 찾지 못함")
		void deleteSoftFailsWhenCommentNotFound() {
			// given
			UUID commentId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> commentService.deleteSoft(commentId, userId))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage("comment not found");
		}
	}

	@Nested
	@DisplayName("댓글 단건 조회(getComment) 테스트")
	class GetCommentTest {

		@Test
		@DisplayName("성공 케이스")
		void getCommentSuccess() {
			// given
			UUID commentId = UUID.randomUUID();
			User mockUser = Mockito.mock(User.class);
			Review mockReview = Mockito.mock(Review.class);
			Comment mockComment = Mockito.mock(Comment.class);

			when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));
			// DTO 변환에 필요한 값들 설정
			when(mockComment.getReview()).thenReturn(mockReview);
			when(mockComment.getUser()).thenReturn(mockUser);

			// when
			CommentDto result = commentService.getComment(commentId);

			// then
			assertThat(result).isNotNull();
			verify(commentRepository).findById(commentId);
		}

		@Test
		@DisplayName("실패 케이스 - 댓글을 찾지 못함")
		void getCommentFailsWhenNotFound() {
			// given
			UUID commentId = UUID.randomUUID();
			when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> commentService.getComment(commentId))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage("comment not found");
		}
	}

	@Nested
	@DisplayName("댓글 물리적 삭제(deleteHard) 테스트")
	class DeleteHardCommentTest {

		@Test
		@DisplayName("성공 케이스")
		void deleteHardSuccess() {
			// given
			UUID commentId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			User mockUser = Mockito.mock(User.class);
			Comment mockComment = Mockito.mock(Comment.class);

			when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));
			when(mockComment.getUser()).thenReturn(mockUser);
			when(mockUser.getId()).thenReturn(userId);

			// when
			commentService.deleteHard(commentId, userId);

			// then
			verify(commentRepository).deleteById(commentId);
		}

		@Test
		@DisplayName("실패 케이스 - 삭제 권한이 없을 때")
		void deleteHardFailsWhenPermissionDenied() {
			// given
			UUID commentId = UUID.randomUUID();
			UUID ownerId = UUID.randomUUID();
			UUID requesterId = UUID.randomUUID(); // 다른 사용자 ID
			User mockUser = Mockito.mock(User.class);
			Comment mockComment = Mockito.mock(Comment.class);

			when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));
			when(mockComment.getUser()).thenReturn(mockUser);
			when(mockUser.getId()).thenReturn(ownerId);

			// when & then
			assertThatThrownBy(() -> commentService.deleteHard(commentId, requesterId))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("해당 댓글을 삭제할 권한이 없습니다.");
		}
	}

	@Nested
	@DisplayName("댓글 목록 조회(getCommentList) 테스트")
	class GetCommentListTest {

		private UUID reviewId;
		private final String direction = "DESC";

		@BeforeEach
		void setUp() {
			reviewId = UUID.randomUUID();
		}

		private Comment createMockComment(UUID id, LocalDateTime createdAt) {
			Comment mockComment = Mockito.mock(Comment.class);
			User mockUser = Mockito.mock(User.class);
			Review mockReview = Mockito.mock(Review.class);

			when(mockComment.getId()).thenReturn(id);
			when(mockComment.getContent()).thenReturn("Test Content");
			when(mockComment.getCreatedAt()).thenReturn(createdAt);
			when(mockComment.getUpdatedAt()).thenReturn(createdAt);
			when(mockComment.getUser()).thenReturn(mockUser);
			when(mockComment.getReview()).thenReturn(mockReview);

			when(mockUser.getId()).thenReturn(UUID.randomUUID());
			when(mockUser.getNickname()).thenReturn("test-user");
			when(mockReview.getId()).thenReturn(this.reviewId);

			return mockComment;
		}

		@Test
		@DisplayName("첫 페이지 조회 시, 다음 페이지가 존재하는 경우")
		void getFirstPage_WithNextPage() {
			int limit = 5;
			int queryLimit = limit + 1;
			LocalDateTime now = LocalDateTime.now();

			List<Comment> mockComments = new java.util.ArrayList<>();
			for (int i = 0; i < limit; i++) {
				mockComments.add(createMockComment(UUID.randomUUID(), now.minusMinutes(i)));
			}
			mockComments.add(Mockito.mock(Comment.class));

			Comment lastCommentInPage = mockComments.get(limit - 1);

			given(commentRepository.findFirstPage(reviewId, direction, queryLimit)).willReturn(
				mockComments);
			given(commentRepository.countByReviewId(reviewId)).willReturn(100L);

			CursorPageResponseCommentDto response = commentService.getCommentList(reviewId,
				direction, null, null, limit);

			assertThat(response.hasNext()).isTrue();
			assertThat(response.nextCursor()).isEqualTo(lastCommentInPage.getId());
			assertThat(response.nextAfter()).isEqualTo(lastCommentInPage.getCreatedAt());
			assertThat(response.totalElements()).isEqualTo(100L);
		}

		@Test
		@DisplayName("마지막 페이지 조회 시, 다음 페이지가 존재하지 않는 경우")
		void getNextPage_AsLastPage() {
			int limit = 5;
			int queryLimit = limit + 1;
			UUID cursorId = UUID.randomUUID();
			LocalDateTime after = LocalDateTime.now();

			List<Comment> mockComments = List.of(
				createMockComment(UUID.randomUUID(), after.minusSeconds(1)),
				createMockComment(UUID.randomUUID(), after.minusSeconds(2))
			);

			given(commentRepository.findNextPage(reviewId, direction, cursorId, after,
				queryLimit)).willReturn(mockComments);
			given(commentRepository.countByReviewId(reviewId)).willReturn(2L);

			CursorPageResponseCommentDto response = commentService.getCommentList(reviewId,
				direction, cursorId, after, limit);

			assertThat(response.hasNext()).isFalse();
			assertThat(response.nextCursor()).isNull();
			assertThat(response.nextAfter()).isNull();
			assertThat(response.totalElements()).isEqualTo(2L);
		}
	}
}
